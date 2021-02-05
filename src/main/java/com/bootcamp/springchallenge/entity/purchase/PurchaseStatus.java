package com.bootcamp.springchallenge.entity.purchase;

import com.bootcamp.springchallenge.exception.BadRequestException;

import java.util.Arrays;

public enum PurchaseStatus {
    PENDING("Pendiente"),
    COMPLETED("Completado"),
    CANCELED("Cancelado");

    private final String value;

    PurchaseStatus(String value) {
        this.value = value;
    }

    public static PurchaseStatus fromValue(String value) throws PurchaseStatusNotFoundException {
        String lcValue = value.toLowerCase();
        return Arrays.stream(values())
                .filter(v -> v.value.toLowerCase().equals(lcValue))
                .findFirst()
                .orElseThrow(() -> new PurchaseStatusNotFoundException(value));
    }

    public String getValue() {
        return value;
    }

    public static class PurchaseStatusNotFoundException extends BadRequestException {
        public static final String MESSAGE = "Estado de compra %s inexistente";
        public PurchaseStatusNotFoundException(String value) {
            super(String.format(MESSAGE, value));
        }
    }
}
