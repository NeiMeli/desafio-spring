package com.bootcamp.springchallenge.service.impl.customer.exception;

import com.bootcamp.springchallenge.exception.BadRequestException;

public class CustomerServiceException extends BadRequestException {

    public CustomerServiceException(CustomerServiceError error) {
        super(error.getMessage());
    }
}
