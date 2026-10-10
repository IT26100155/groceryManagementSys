package com.grocerymanagementsys.order.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Integer orderId) {
        super("Invalid orderId: " + orderId);
    }
}
