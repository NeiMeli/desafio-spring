package com.bootcamp.springchallenge.controller.customer.dto;

public class CustomerResponseDTO {
    private String userName;
    private String province;

    public String getUserName() {
        return userName;
    }

    public CustomerResponseDTO setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public CustomerResponseDTO setProvince(String province) {
        this.province = province;
        return this;
    }
}
