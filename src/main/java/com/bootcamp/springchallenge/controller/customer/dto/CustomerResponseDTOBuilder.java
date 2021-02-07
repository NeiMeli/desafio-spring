package com.bootcamp.springchallenge.controller.customer.dto;

import com.bootcamp.springchallenge.entity.customer.Customer;

public class CustomerResponseDTOBuilder {
    private final Customer customer;

    public CustomerResponseDTOBuilder(Customer customer) {
        this.customer = customer;
    }

    public CustomerResponseDTO build() {
        return new CustomerResponseDTO().setUserName(customer.getUserName()).setProvince(customer.getProvince().getLabel());
    }
}
