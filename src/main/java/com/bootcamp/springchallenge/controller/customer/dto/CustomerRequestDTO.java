package com.bootcamp.springchallenge.controller.customer.dto;

public class CustomerRequestDTO {
    private String userName;
    private String province;

    public String getUserName() {
        return userName;
    }

    public CustomerRequestDTO setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public CustomerRequestDTO setProvince(String province) {
        this.province = province;
        return this;
    }
}
