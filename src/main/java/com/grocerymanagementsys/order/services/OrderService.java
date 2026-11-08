package com.grocerymanagementsys.order.services;

import com.grocerymanagementsys.address.entities.Address;
import com.grocerymanagementsys.cart.dtos.CartItemDto;
import com.grocerymanagementsys.cart.dtos.CartResponseDto;
import com.grocerymanagementsys.cart.services.CartService;
import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.customer.entities.Customer;
import com.grocerymanagementsys.customer.services.CustomerService;
import com.grocerymanagementsys.inventory.repositories.ItemRepository;
import com.grocerymanagementsys.order.dtos.OrderResponseDto;
import com.grocerymanagementsys.order.dtos.OrderUnitDto;
import com.grocerymanagementsys.order.dtos.PlaceOrderDto;
import com.grocerymanagementsys.order.dtos.StaffOrderDto;
import com.grocerymanagementsys.order.entities.Order;
import com.grocerymanagementsys.order.entities.OrderUnit;
import com.grocerymanagementsys.order.enums.OrderStatus;
import com.grocerymanagementsys.order.exceptions.StockNotSufficient;
import com.grocerymanagementsys.order.repositories.OrderRepository;
import com.grocerymanagementsys.payment.enums.PaymentState;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CustomerService customerService;
    private final ItemRepository itemRepository;


    @Transactional
    public OrderResponseDto placeOrder(PlaceOrderDto dto) {
        Customer customer = customerService.findCustomerOrThrow(dto.customerId());

        Address address = customer.getAddresses().stream()
                .filter(a -> a.getId().equals(dto.addressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address", dto.addressId()));

        CartResponseDto cart = cartService.getCart(dto.customerId());
        if (cart.items().isEmpty()) {
            throw new ResourceNotFoundException("Cannot place an order with an empty cart");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(cart.total());
        order.setDate(LocalDateTime.now());

        for (CartItemDto line : cart.items()) {
            if (itemRepository.decrementStock(line.itemId(), line.quantity()) == 0) {
                throw new StockNotSufficient(line.itemId(), line.itemName());
            }

            OrderUnit unit = new OrderUnit();
            unit.setOrder(order);
            unit.setItem(itemRepository.getReferenceById(line.itemId()));
            unit.setQuantity(line.quantity());
            unit.setUnitPrice(line.discountedUnitPrice());
            order.getOrderUnits().add(unit);
        }

        order = orderRepository.save(order);
        cartService.clearCart(dto.customerId());

        return toResponse(order);
    }

    @Transactional
    public OrderResponseDto cancelOrderById(Integer id) {
        Order order = findOrderOrThrow(id);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ValidationException("Order " + id + " is already cancelled");
        }
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new ValidationException("Order " + id + " is " + order.getStatus() + " and can no longer be cancelled");
        }

        for (OrderUnit unit : order.getOrderUnits()) {
            itemRepository.incrementStock(unit.getItem().getId(), unit.getQuantity());
        }
        order.getPayments().stream()
                .filter(p -> p.getState() == PaymentState.PENDING)
                .forEach(p -> p.setState(PaymentState.CANCELLED));

        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getById(Integer id) {
        return toResponse(findOrderOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getByCustomer(Integer customerId) {
        return customerService.findCustomerOrThrow(customerId).getOrders().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getByPaymentState(PaymentState state) {
        return orderRepository.findByPaymentState(state).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<StaffOrderDto> getStaffOrdersByPaymentState(PaymentState state) {
        return orderRepository.findByPaymentState(state).stream().map(this::toStaffDto).toList();
    }

    @Transactional
    public void updateStatus(Integer id, OrderStatus newStatus) {
        if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.DELIVERED) {
            throw new ValidationException("An order can be set to Confirmed, Shipped or Delivered here.");
        }
        Order order = findOrderOrThrow(id);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ValidationException("Order " + id + " is cancelled and can't be changed.");
        }
        boolean paid = order.getPayments().stream().anyMatch(p -> p.getState() == PaymentState.CONFIRMED);
        if (!paid) {
            throw new ValidationException("Order " + id + " has no verified payment yet.");
        }
        order.setStatus(newStatus);
    }

    private StaffOrderDto toStaffDto(Order order) {
        List<StaffOrderDto.Line> lines = order.getOrderUnits().stream()
                .map(u -> new StaffOrderDto.Line(u.getItem().getName(), u.getQuantity(), u.getUnitPrice()))
                .toList();
        return new StaffOrderDto(
                order.getId(),
                order.getCustomer().getFullName(),
                order.getCustomer().getEmail(),
                order.getAddress() == null ? null : order.getAddress().getAddressBody(),
                order.getDate(),
                order.getTotal(),
                order.getStatus(),
                lines
        );
    }

    private Order findOrderOrThrow(Integer id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    private OrderResponseDto toResponse(Order order) {
        List<OrderUnitDto> units = order.getOrderUnits().stream()
                .map(u -> new OrderUnitDto(u.getItem().getId(), u.getQuantity(), u.getUnitPrice()))
                .toList();

        return new OrderResponseDto(
                order.getId(),
                order.getCustomer().getId(),
                order.getStatus(),
                order.getTotal(),
                order.getDate(),
                units
        );
    }
}
