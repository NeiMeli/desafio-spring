package com.bootcamp.springchallenge.controller.purchase.dto.request;

public class PurchaseRequestArticleDTO {
    private int articleId;
    private double discount;
    private int quantity;

    public PurchaseRequestArticleDTO() {
        discount = 0;
    }

    public int getArticleId() {
        return articleId;
    }

    public PurchaseRequestArticleDTO setArticleId(int articleId) {
        this.articleId = articleId;
        return this;
    }

    public double getDiscount() {
        return discount;
    }

    public PurchaseRequestArticleDTO setDiscount(double discount) {
        this.discount = discount;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public PurchaseRequestArticleDTO setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }
}
