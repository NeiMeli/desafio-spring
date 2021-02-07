package com.bootcamp.springchallenge.service.impl.customer.exception;

public enum CustomerServiceErrorImpl implements CustomerServiceError {
    CUSTOMER_ALREADY_EXISTS("Ya existe un usuario con ese nombre");

    private final String message;

    CustomerServiceErrorImpl(String s) {
        this.message = s;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
