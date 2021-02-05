package com.bootcamp.springchallenge.controller.purchase.dto.request;

public class PurchaseDataDTO {
    private String userName;
    private int receipt;

    public String getUserName() {
        return userName;
    }

    public PurchaseDataDTO setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public int getReceipt() {
        return receipt;
    }

    public PurchaseDataDTO setReceipt(int receipt) {
        this.receipt = receipt;
        return this;
    }
}
