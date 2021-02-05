package com.bootcamp.springchallenge.controller.articlequery.dto;

public class ArticleResponseDTO {
    private int id;
    private String name;
    private String category;
    private double price;
    private String prestige;
    private boolean freeShipping;

    public ArticleResponseDTO(int id, String name, String category, double price, String prestige, boolean freeShipping) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.prestige = prestige;
        this.freeShipping = freeShipping;
    }

    public ArticleResponseDTO() {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPrestige() {
        return prestige;
    }

    public void setPrestige(String prestige) {
        this.prestige = prestige;
    }

    public void setFreeShipping(boolean freeShipping) {
        this.freeShipping = freeShipping;
    }

    public boolean getFreeShipping() {
        return freeShipping;
    }
}
