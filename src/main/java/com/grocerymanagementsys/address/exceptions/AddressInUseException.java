package com.grocerymanagementsys.address.exceptions;

public class AddressInUseException extends RuntimeException {
    public AddressInUseException(Integer addressId) {
        super("Address " + addressId + " is used by an order and cannot be deleted.");
    }
}
