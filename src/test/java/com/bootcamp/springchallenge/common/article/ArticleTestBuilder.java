package com.bootcamp.springchallenge.common.article;

import com.bootcamp.springchallenge.entity.article.Article;
import com.bootcamp.springchallenge.entity.article.Category;
import com.bootcamp.springchallenge.entity.article.Prestige;

public class ArticleTestBuilder {
    private int id;
    private String name;
    private Category category;
    private String brand;
    private double price;
    private int stock;
    private boolean freeShipping;
    private Prestige prestige;

    public static ArticleTestBuilder fromArticle(Article article) {
        return new ArticleTestBuilder()
                .withId(article.getId())
                .withName(article.getName())
                .withCategory(article.getCategory())
                .withBrand(article.getBrand())
                .withPrice(article.getPrice())
                .withStock(article.getStock())
                .withFreeShipping(article.isFreeShipping())
                .withPrestige(article.getPrestige());
    }

    public Article build() {
        Article article = new Article();
        article.setName(name);
        article.setId(id);
        article.setCategory(category);
        article.setBrand(brand);
        article.setPrice(price);
        article.setStock(stock);
        article.setFreeShipping(freeShipping);
        article.setPrestige(prestige);
        return article;
    }

    public ArticleTestBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }

    public ArticleTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ArticleTestBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ArticleTestBuilder withBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public ArticleTestBuilder withPrice(double price) {
        this.price = price;
        return this;
    }

    public ArticleTestBuilder withStock(int stock) {
        this.stock = stock;
        return this;
    }

    public ArticleTestBuilder withFreeShipping(boolean freeShipping) {
        this.freeShipping = freeShipping;
        return this;
    }

    public ArticleTestBuilder withPrestige(Prestige prestige) {
        this.prestige = prestige;
        return this;
    }
}
