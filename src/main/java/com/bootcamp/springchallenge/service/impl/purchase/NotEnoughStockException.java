package com.bootcamp.springchallenge.service.impl.purchase;

public class NotEnoughStockException extends Exception {
    public NotEnoughStockException(String message) {
        super(message);
    }
}
