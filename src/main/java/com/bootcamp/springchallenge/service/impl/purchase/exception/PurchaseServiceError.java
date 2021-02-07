package com.bootcamp.springchallenge.service.impl.purchase.exception;

public interface PurchaseServiceError {
    String getMessage();
    String getMessage(Object ... args);
}
