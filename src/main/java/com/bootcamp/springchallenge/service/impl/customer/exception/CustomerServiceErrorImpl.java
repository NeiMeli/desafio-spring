package com.bootcamp.springchallenge.service.impl.customer.exception;

public enum CustomerServiceErrorImpl implements CustomerServiceError {
    EMPTY_USERNAME("Nombre de usuario vacio"),
    CUSTOMER_ALREADY_EXISTS("Ya existe un usuario con ese nombre"),
    CUSTOMER_NOT_FOUND("Cliente no encontrado");

    private final String message;

    CustomerServiceErrorImpl(String s) {
        this.message = s;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
