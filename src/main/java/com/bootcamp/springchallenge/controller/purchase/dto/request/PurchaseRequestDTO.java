package com.bootcamp.springchallenge.controller.purchase.dto.request;


import java.util.List;

public class PurchaseRequestDTO {
    private String userName;
    private List<PurchaseRequestArticleDTO> articles;

    public String getUserName() {
        return userName;
    }

    public PurchaseRequestDTO setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public List<PurchaseRequestArticleDTO> getArticles() {
        return articles;
    }

    public PurchaseRequestDTO setArticles(List<PurchaseRequestArticleDTO> articles) {
        this.articles = articles;
        return this;
    }
}
