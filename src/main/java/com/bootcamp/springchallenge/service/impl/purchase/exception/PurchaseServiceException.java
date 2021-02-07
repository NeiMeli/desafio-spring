package com.bootcamp.springchallenge.service.impl.purchase.exception;

import com.bootcamp.springchallenge.exception.BadRequestException;

public class PurchaseServiceException extends BadRequestException {
    public PurchaseServiceException(PurchaseServiceError error, Object ... args) {
        super(error.getMessage(args));
    }
}
