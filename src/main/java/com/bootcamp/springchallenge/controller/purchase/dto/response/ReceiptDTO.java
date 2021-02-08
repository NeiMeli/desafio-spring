package com.bootcamp.springchallenge.controller.purchase.dto.response;

 import java.util.List;

public class ReceiptDTO {
    private int id;
    private String status;
    private List<PurchaseResponseArticleDTO> articles;

    private double total;

    public double getTotal() {
        return total;
    }

    public ReceiptDTO() {
    }

    public int getId() {
        return id;
    }

    public ReceiptDTO setId(int id) {
        this.id = id;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ReceiptDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public List<PurchaseResponseArticleDTO> getArticles() {
        return articles;
    }

    public void setArticles(List<PurchaseResponseArticleDTO> articles) {
        this.articles = articles;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
