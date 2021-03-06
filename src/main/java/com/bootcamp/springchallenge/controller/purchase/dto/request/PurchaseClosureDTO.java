package com.bootcamp.springchallenge.controller.purchase.dto.request;

public class PurchaseClosureDTO {
    private String userName;
    private int receipt;
    private boolean useBonus;

    public boolean isUseBonus() {
        return useBonus;
    }

    public PurchaseClosureDTO setUseBonus(boolean useBonus) {
        this.useBonus = useBonus;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public PurchaseClosureDTO setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public int getReceipt() {
        return receipt;
    }

    public PurchaseClosureDTO setReceipt(int receipt) {
        this.receipt = receipt;
        return this;
    }
}
