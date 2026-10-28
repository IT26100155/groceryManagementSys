package com.grocerymanagementsys.order.services;

<<<<<<< Updated upstream
import com.grocerymanagementsys.address.repositories.AddressRepository;  //Assumption
import com.grocerymanagementsys.cart.dtos.CartItemDto;  //Assumption
import com.grocerymanagementsys.cart.dtos.CartResponseDto;  //Assumption
import com.grocerymanagementsys.cart.services.CartService;  //Assumption
import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.order.dtos.OrderResponseDto;
import com.grocerymanagementsys.order.dtos.OrderUnitDto;
import com.grocerymanagementsys.order.dtos.PlaceOrderDto;
import com.grocerymanagementsys.order.entities.Order;
import com.grocerymanagementsys.order.entities.OrderUnit;
import com.grocerymanagementsys.order.enums.OrderStatus;
import com.grocerymanagementsys.order.repositories.OrderRepository;
import com.grocerymanagementsys.order.repositories.OrderUnitRepository;
=======
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
>>>>>>> Stashed changes
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
<<<<<<< Updated upstream
    private final OrderUnitRepository orderUnitRepository;
    private final CartService cartService;
    private final AddressRepository addressRepository;

    @Transactional
    public OrderResponseDto placeOrder(PlaceOrderDto dto) {
        addressRepository.findByIdAndCustomerId(dto.addressId(), dto.customerId())
=======
    private final CartService cartService;
    private final CustomerService customerService;
    private final ItemRepository itemRepository;


    @Transactional
    public OrderResponseDto placeOrder(PlaceOrderDto dto) {
        Customer customer = customerService.findCustomerOrThrow(dto.customerId());

        Address address = customer.getAddresses().stream()
                .filter(a -> a.getId().equals(dto.addressId()))
                .findFirst()
>>>>>>> Stashed changes
                .orElseThrow(() -> new ResourceNotFoundException("Address", dto.addressId()));

        CartResponseDto cart = cartService.getCart(dto.customerId());
        if (cart.items().isEmpty()) {
            throw new ResourceNotFoundException("Cannot place an order with an empty cart");
        }

        Order order = new Order();
<<<<<<< Updated upstream
        order.setCustomerId(dto.customerId());
        order.setAddressId(dto.addressId());
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(cart.total());
        order.setDate(LocalDateTime.now());
        order = orderRepository.save(order);

        for (CartItemDto line : cart.items()) {
            OrderUnit unit = new OrderUnit();
            unit.setOrderId(order.getId());
            unit.setItemId(line.itemId());
            unit.setQuantity(line.quantity());
            unit.setUnitPrice(line.discountedUnitPrice());
            orderUnitRepository.save(unit);
        }

=======
        order.setCustomer(customer);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(cart.total());
        order.setDate(LocalDateTime.now());

        for (CartItemDto line : cart.items()) {
            // Atomic: only succeeds while enough stock is left, so concurrent buyers can't oversell.
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
>>>>>>> Stashed changes
        cartService.clearCart(dto.customerId());

        return toResponse(order);
    }

<<<<<<< Updated upstream
    public OrderResponseDto cancelOrderById(Integer id) {
        Order order = findOrderOrThrow(id);
        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    public OrderResponseDto getById(Integer id) {
        Order order = findOrderOrThrow(id);
        return toResponse(order);
=======
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

    /** Orders whose payment was verified (confirmed), with the details staff need to process them. */
    @Transactional(readOnly = true)
    public List<StaffOrderDto> getStaffOrdersByPaymentState(PaymentState state) {
        return orderRepository.findByPaymentState(state).stream().map(this::toStaffDto).toList();
    }

    /** Staff moves a paid order between Confirmed, Shipped and Delivered. Cancelling has its own flow. */
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
>>>>>>> Stashed changes
    }

    private Order findOrderOrThrow(Integer id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

<<<<<<< Updated upstream
    public List<OrderResponseDto> getByCustomer(Integer customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponseDto toResponse(Order order) {
        List<OrderUnitDto> units = orderUnitRepository.findByOrderId(order.getId()).stream()
                .map(
                        u -> new OrderUnitDto(u.getItemId(), u.getQuantity(), u.getUnitPrice())
                ).toList();

        return new OrderResponseDto(
                order.getId(),
                order.getCustomerId(),
=======
    private OrderResponseDto toResponse(Order order) {
        List<OrderUnitDto> units = order.getOrderUnits().stream()
                .map(u -> new OrderUnitDto(u.getItem().getId(), u.getQuantity(), u.getUnitPrice()))
                .toList();

        return new OrderResponseDto(
                order.getId(),
                order.getCustomer().getId(),
>>>>>>> Stashed changes
                order.getStatus(),
                order.getTotal(),
                order.getDate(),
                units
        );
    }
}
