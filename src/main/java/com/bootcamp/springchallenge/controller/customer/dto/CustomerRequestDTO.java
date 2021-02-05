package com.bootcamp.springchallenge.controller.customer.dto;

public class CustomerRequestDTO {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public CustomerRequestDTO setUserName(String userName) {
        this.userName = userName;
        return this;
    }
}
