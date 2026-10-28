package com.grocerymanagementsys.payment.exceptions;

public class ConfirmedPaymentExistsException extends RuntimeException {
    public ConfirmedPaymentExistsException(Integer orderId) {
        super("Confirmed Payment already exists for orderId: "+orderId);
    }
}
