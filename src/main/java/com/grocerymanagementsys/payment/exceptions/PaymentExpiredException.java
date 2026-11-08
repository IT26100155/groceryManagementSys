package com.grocerymanagementsys.payment.exceptions;

import jakarta.validation.ValidationException;

public class PaymentExpiredException extends ValidationException {
    public PaymentExpiredException(Integer paymentId) {
        super("payment id: "+paymentId+" expired.");
    }
}
