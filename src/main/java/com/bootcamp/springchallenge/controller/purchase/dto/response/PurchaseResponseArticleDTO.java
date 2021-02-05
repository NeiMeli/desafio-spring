package com.bootcamp.springchallenge.controller.purchase.dto.response;

public class PurchaseResponseArticleDTO {
    private int id;
    private String name;
    private int quantity;
    private double cost;

    public PurchaseResponseArticleDTO() {
    }

    public int getId() {
        return id;
    }

    public PurchaseResponseArticleDTO setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PurchaseResponseArticleDTO setName(String name) {
        this.name = name;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public PurchaseResponseArticleDTO setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public double getCost() {
        return cost;
    }

    public PurchaseResponseArticleDTO setCost(double cost) {
        this.cost = cost;
        return this;
    }
}
