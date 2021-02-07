package com.bootcamp.springchallenge.entity.purchase;

import com.bootcamp.springchallenge.controller.common.util.DoubleProcessor;
import com.bootcamp.springchallenge.entity.Persistable;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.bootcamp.springchallenge.entity.purchase.PurchaseStatus.*;

public class Purchase implements Persistable<Integer> {
    private int id;
    private String userName;
    private final List<PurchaseArticle> articles;
    private PurchaseStatus status;
    public Purchase() {
        articles = new ArrayList<>();
    }

    public Purchase(String userName) {
        this.userName = userName;
        this.status = PENDING;
        this.articles = new ArrayList<>();
    }

    public static Purchase fromJson(JsonNode jn) {
        final Purchase po = new Purchase();
        po.setId(jn.get("id").intValue());
        po.setUserName(jn.get("userName").textValue());
        JsonNode articles = jn.get("articles");
        for (JsonNode article : articles) {
            po.getArticles().add(PurchaseArticle.fromJson(article));
        }
        po.setStatus(PurchaseStatus.fromValue(jn.get("status").textValue()));
        return po;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Integer getPrimaryKey() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<PurchaseArticle> getArticles() {
        return articles;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    private void setStatus(PurchaseStatus status) {
        this.status = status;
    }
    
    public boolean isPending() {
        return status == PENDING;
    }

    public boolean isCompleted() {
        return status == COMPLETED;
    }
    
    public void complete() {
        setStatus(COMPLETED);
    }

    public int getId() {
        return id;
    }

    public void addArticle(int articleId, String articleName, int quantity, double discount, double price) {
        Optional<PurchaseArticle> existingArticleOpt = articles.stream()
                .filter(a -> a.getArticleId() == articleId && DoubleProcessor.equal(a.getDiscount(), discount))
                .findFirst();
        if (existingArticleOpt.isPresent()) {
            existingArticleOpt.get().setPrice(price).add(quantity); // actualizo el precio
        } else {
            PurchaseArticle newPurchaseArticle = new PurchaseArticle(articleId, articleName, quantity, discount, price);
            articles.add(newPurchaseArticle);
        }
    }

    @Override
    public boolean isNew() {
        return id == 0;
    }

    @Override
    public void setId(@NotNull Integer id) {
        this.id = id;
    }

    public void cancel() {
        setStatus(PurchaseStatus.CANCELED);
    }

    public boolean isCanceled() {
        return status == CANCELED;
    }

    public void applyBonus() {
        articles.forEach(a -> a.addDiscount(5));
    }
}
