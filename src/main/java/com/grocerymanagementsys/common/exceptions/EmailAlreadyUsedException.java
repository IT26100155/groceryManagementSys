package com.grocerymanagementsys.common.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("Account with "+email+" email already exists.");
    }
}
