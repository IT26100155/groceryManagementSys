package com.grocerymanagementsys.order.services;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderUnitRepository orderUnitRepository;
    private final CartService cartService;
    private final AddressRepository addressRepository;

    @Transactional
    public OrderResponseDto placeOrder(PlaceOrderDto dto) {
        addressRepository.findByIdAndCustomerId(dto.addressId(), dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", dto.addressId()));

        CartResponseDto cart = cartService.getCart(dto.customerId());
        if (cart.items().isEmpty()) {
            throw new ResourceNotFoundException("Cannot place an order with an empty cart");
        }

        Order order = new Order();
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

        cartService.clearCart(dto.customerId());

        return toResponse(order);
    }

    public OrderResponseDto getById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return toResponse(order);
    }

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
                order.getStatus(),
                order.getTotal(),
                order.getDate(),
                units
        );
    }
}
