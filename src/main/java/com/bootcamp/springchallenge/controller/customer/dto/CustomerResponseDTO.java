package com.bootcamp.springchallenge.controller.customer.dto;

public class CustomerResponseDTO {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public CustomerResponseDTO setUserName(String userName) {
        this.userName = userName;
        return this;
    }
}
