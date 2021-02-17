package com.bootcamp.springchallenge.entity.article;

import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;
import com.bootcamp.springchallenge.entity.Persistable;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

public class Article implements Persistable<Integer> {
    private int id;
    private String name;
    private Category category;
    private String brand;
    private double price;
    private int stock;
    private boolean freeShipping;
    private Prestige prestige;

    public static Article fromJson(JsonNode jn) {
        final Article article = new Article();
        article.setId(jn.get("id").intValue());
        article.setName(jn.get("name").textValue());
        article.setCategory(Category.fromValue(jn.get("category").textValue()));
        article.setBrand(jn.get("brand").textValue());
        article.setPrice(jn.get("price").doubleValue());
        article.setStock(jn.get("stock").intValue());
        article.setFreeShipping(jn.get("freeShipping").asBoolean());
        article.setPrestige(Prestige.fromValue(jn.get("prestige").intValue()));
        return article;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }

    public boolean hasStock(int amount) {
        return this.stock >= amount;
    }

    public void reserveStock(int amount) {
        setStock(stock - amount);
    }

    public void releaseStock(int amount) {
        setStock(stock + amount);
    }

    public void setFreeShipping(boolean freeShipping) {
        this.freeShipping = freeShipping;
    }

    public Prestige getPrestige() {
        return prestige;
    }

    public void setPrestige(Prestige prestige) {
        this.prestige = prestige;
    }

    @Override
    public Integer getPrimaryKey() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id == 0;
    }

    @Override
    public void setId(@NotNull Integer id) {
        this.id = id;
    }

    public ArticleResponseDTO toDTO() {
        ArticleResponseDTO dto = new ArticleResponseDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setPrice(price);
        dto.setCategory(category.getValue());
        dto.setPrestige(prestige.toPrettyString());
        dto.setFreeShipping(freeShipping);
        return dto;
    }

    public String describe() {
        return id + " - " + name + " - " + brand;
    }
}
