package com.grocerymanagementsys.payment.services;

import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.order.entities.Order;
import com.grocerymanagementsys.order.enums.OrderStatus;
import com.grocerymanagementsys.order.exceptions.OrderNotFoundException;
import com.grocerymanagementsys.order.repositories.OrderRepository;
import com.grocerymanagementsys.payment.dtos.InitiatePaymentDto;
import com.grocerymanagementsys.payment.dtos.PaymentResponseDto;
import com.grocerymanagementsys.payment.entities.Payment;
import com.grocerymanagementsys.payment.enums.PaymentState;
import com.grocerymanagementsys.payment.exceptions.ConfirmedPaymentExistsException;
import com.grocerymanagementsys.payment.exceptions.PaymentExpiredException;
import com.grocerymanagementsys.payment.exceptions.PendingPaymentExistsException;
import com.grocerymanagementsys.payment.repositories.PaymentRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponseDto initiate(Integer orderId, InitiatePaymentDto dto) {
        Order order = findOrderOrThrow(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ValidationException("Order " + orderId + " is cancelled and can no longer be paid");
        }

        order.getPayments().forEach(this::expireIfNeeded);

        if (hasPaymentInState(order, PaymentState.PENDING)) {
            throw new PendingPaymentExistsException(orderId);
        }
        if (hasPaymentInState(order, PaymentState.CONFIRMED)) {
            throw new ConfirmedPaymentExistsException(orderId);
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setType(dto.type());
        payment.setDate(LocalDateTime.now());
        payment.setExpiryTime(LocalDateTime.now().plusHours(3));
        payment.setState(PaymentState.PENDING);

        order.getPayments().add(payment);
        return toPaymentResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponseDto confirm(Integer id) {
        Payment payment = expireIfNeeded(findPaymentOrThrow(id));
        if (payment.getState() == PaymentState.EXPIRED) {
            throw new PaymentExpiredException(id);
        }

        requirePending(payment, "confirmed");
        payment.setState(PaymentState.CONFIRMED);

        Order order = payment.getOrder();
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        return toPaymentResponse(payment);
    }

    @Transactional
    public PaymentResponseDto cancel(Integer id) {
        Payment payment = expireIfNeeded(findPaymentOrThrow(id));
        requirePending(payment, "cancelled");
        payment.setState(PaymentState.CANCELLED);
        return toPaymentResponse(payment);
    }

    @Transactional
    public List<PaymentResponseDto> getPending() {
        return paymentRepository.findByState(PaymentState.PENDING).stream()
                .map(this::expireIfNeeded)
                .filter(p -> p.getState() == PaymentState.PENDING)
                .map(this::toPaymentResponse)
                .toList();
    }

    private void requirePending(Payment payment, String action) {
        if (payment.getState() != PaymentState.PENDING) {
            throw new ValidationException("Only a pending payment can be " + action + " (this one is " + payment.getState() + ")");
        }
    }

    @Transactional
    public PaymentResponseDto getById(Integer id) {
        return toPaymentResponse(expireIfNeeded(findPaymentOrThrow(id)));
    }

    @Transactional
    public List<PaymentResponseDto> getByOrder(Integer orderId) {
        return findOrderOrThrow(orderId).getPayments().stream()
                .map(this::expireIfNeeded)
                .map(this::toPaymentResponse)
                .toList();
    }

    @Transactional
    public boolean isOrderPaid(Integer orderId) {
        Order order = findOrderOrThrow(orderId);
        order.getPayments().forEach(this::expireIfNeeded);
        return hasPaymentInState(order, PaymentState.CONFIRMED);
    }

    private boolean hasPaymentInState(Order order, PaymentState state) {
        return order.getPayments().stream().anyMatch(p -> p.getState() == state);
    }

    private Order findOrderOrThrow(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private Payment findPaymentOrThrow(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }

    private Payment expireIfNeeded(Payment payment) {
        if (payment.getState() == PaymentState.PENDING && payment.getExpiryTime().isBefore(LocalDateTime.now())) {
            payment.setState(PaymentState.EXPIRED);
        }
        return payment;
    }

    private PaymentResponseDto toPaymentResponse(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getType(),
                payment.getDate(),
                payment.getExpiryTime(),
                payment.getState()
        );
    }
}
