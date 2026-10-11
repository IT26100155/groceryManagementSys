package com.grocerymanagementsys.payment.services;

import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.order.exceptions.OrderNotFoundException;
import com.grocerymanagementsys.order.repositories.OrderRepository;
import com.grocerymanagementsys.payment.dtos.InitiatePaymentDto;
import com.grocerymanagementsys.payment.dtos.PaymentResponseDto;
import com.grocerymanagementsys.payment.entities.Payment;
import com.grocerymanagementsys.payment.enums.PaymentState;
import com.grocerymanagementsys.payment.exceptions.ConfirmedPaymentExistsException;
import com.grocerymanagementsys.payment.exceptions.PendingPaymentExistsException;
import com.grocerymanagementsys.payment.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentResponseDto initiate(Integer orderId, InitiatePaymentDto dto) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException(orderId);
        }

        expireStalePayments(orderId);

        if (paymentRepository.existsByOrderIdAndState(orderId, PaymentState.PENDING)) {
            throw new PendingPaymentExistsException(orderId);
        }
        if (paymentRepository.existsByOrderIdAndState(orderId, PaymentState.CONFIRMED)) {
            throw new ConfirmedPaymentExistsException(orderId);
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setType(dto.type());
        payment.setDate(LocalDateTime.now());
        payment.setExpiryTime(LocalDateTime.now().plusHours(3));
        payment.setState(PaymentState.PENDING);

        return toPaymentResponse(paymentRepository.save(payment));
    }

    public PaymentResponseDto confirm(Integer id) {
        Payment payment = findPaymentOrThrow(id);
        payment.setState(PaymentState.CONFIRMED);
        return toPaymentResponse(paymentRepository.save(payment));
    }

    public PaymentResponseDto cancel(Integer id) {
        Payment payment = findPaymentOrThrow(id);
        payment.setState(PaymentState.CANCELLED);
        return toPaymentResponse(paymentRepository.save(payment));
    }

    public PaymentResponseDto getById(Integer id) {
        return toPaymentResponse(expireIfNeeded(findPaymentOrThrow(id)));
    }

    public List<PaymentResponseDto> getByOrder(Integer orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(this::expireIfNeeded)
                .map(this::toPaymentResponse)
                .toList();
    }

    public boolean isOrderPaid(Integer orderId) {
        return paymentRepository.existsByOrderIdAndState(orderId, PaymentState.CONFIRMED);
    }

    private Payment findPaymentOrThrow(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }

    private Payment expireIfNeeded(Payment payment) {
        if (payment.getState() == PaymentState.PENDING && payment.getExpiryTime().isBefore(LocalDateTime.now())) {
            payment.setState(PaymentState.EXPIRED);
            paymentRepository.save(payment);
        }
        return payment;
    }

    private void expireStalePayments(Integer orderId) {
        paymentRepository.findByOrderId(orderId).forEach(this::expireIfNeeded);
    }

    private PaymentResponseDto toPaymentResponse(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getType(),
                payment.getDate(),
                payment.getExpiryTime(),
                payment.getState()
        );
    }
}
