package com.grocerymanagementsys.payment.exceptions;

public class PendingPaymentExistsException extends RuntimeException {
    public PendingPaymentExistsException(Integer orderId) {
        super("pending payment for order Id: " + orderId);
    }
}
