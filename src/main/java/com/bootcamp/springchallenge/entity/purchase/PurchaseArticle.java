package com.bootcamp.springchallenge.entity.purchase;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.Nullable;

import static com.bootcamp.springchallenge.controller.common.util.DoubleProcessor.roundTwoDecimals;

public class PurchaseArticle {
    private int articleId;
    private String articleName;
    private double price;
    private int quantity;
    private double discount;

    public PurchaseArticle() {}

    public PurchaseArticle(int articleId, String articleName, int quantity, @Nullable Double discount, double price) {
        this.articleId = articleId;
        this.articleName = articleName;
        this.quantity = quantity;
        this.discount = discount != null ? discount : 0;
        this.price = price;
    }

    public static PurchaseArticle fromJson(JsonNode article) {
        PurchaseArticle dto = new PurchaseArticle();
        dto.setArticleId(article.get("articleId").intValue());
        dto.setArticleName(article.get("articleName").textValue());
        dto.setDiscount(article.get("discount").doubleValue());
        dto.setQuantity(article.get("quantity").intValue());
        dto.setPrice(article.get("price").doubleValue());
        return dto;
    }

    private void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void add(int quantity) {
        setQuantity(this.quantity + quantity);
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PurchaseArticle)) return false;
        PurchaseArticle other = (PurchaseArticle) obj;
        return this.articleId == other.articleId && Double.compare(this.discount, other.discount) == 0;
    }


    public double calculateCost() {
        double discountMultiplier = 1 - (discount / 100);
        return roundTwoDecimals((price * quantity) * discountMultiplier);
    }

    public String getArticleName() {
        return articleName;
    }

    public PurchaseArticle setPrice(double price) {
        this.price = price;
        return this;
    }

    public void addDiscount(int discount) {
        if (this.discount + discount < 100) {
            this.discount += discount;
        };
    }
}
