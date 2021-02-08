package com.bootcamp.springchallenge.service.impl.article.query;

import com.bootcamp.springchallenge.entity.article.Article;
import com.bootcamp.springchallenge.entity.article.Category;
import com.bootcamp.springchallenge.entity.article.Prestige;
import com.bootcamp.springchallenge.service.impl.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.bootcamp.springchallenge.service.impl.article.query.ArticleQueryParam.BRAND;

public class ArticleQuery extends Query<ArticleQueryParam, Article> {
    protected @NotNull OrderType orderType;

    public ArticleQuery() {
        super();
        orderType = OrderType.NONE;
    }

    public ArticleQuery withName(@Nullable String name) {
        if (name != null) filters.put(ArticleQueryParam.NAME, p -> p.getName().toLowerCase().contains(name.toLowerCase()));
        return this;
    }

    public ArticleQuery without(ArticleQueryParam param) {
        filters.remove(param);
        return this;
    }

    public ArticleQuery withCategories(@Nullable String... categories) {
        if (categories == null || categories.length == 0) return this;
        List<Category> categoryList = Arrays.stream(categories).map(Category::fromValue).collect(Collectors.toList());
        return withCategories(categoryList);
    }

    public ArticleQuery withCategories(List<Category> categories) {
        if (categories.size() > 0) {
            filters.put(ArticleQueryParam.CATEGORY, a -> categories.contains(a.getCategory()));
        }
        return this;
    }

    public ArticleQuery withMaxPrice(@Nullable Double maxPrice) {
        if (maxPrice != null) filters.put(ArticleQueryParam.MAX_PRICE, p -> Double.compare(p.getPrice(), maxPrice) <= 0);
        return this;
    }

    public ArticleQuery withFreeShipping(@Nullable Boolean freeShipping) {
        if (freeShipping != null) filters.put(ArticleQueryParam.FREE_SHIPPING, p -> p.isFreeShipping() == freeShipping);
        return this;
    }

    public ArticleQuery withMinPrestige(@Nullable Integer minPrestige) {
        if (minPrestige != null) {
            Prestige prest = Prestige.fromValue(minPrestige); // lo busco para validar
            filters.put(ArticleQueryParam.MIN_PRESTIGE, p -> p.getPrestige().getValue() >= prest.getValue());
        }
        return this;
    }

    public ArticleQuery withStockAvailable(@Nullable Integer stockAvailable) {
        if (stockAvailable != null) filters.put(ArticleQueryParam.STOCK_AVAILABLE, p -> p.hasStock(stockAvailable));
        return this;
    }

    public ArticleQuery withOrder(@Nullable Integer order) {
        if (order != null) {
            orderType = OrderType.fromId(order);
        } else {
            orderType = OrderType.NONE;
        }
        return this;
    }

    @Override
    public Comparator<Article> getComparator() {
        return orderType.getComparator();
    }

    public ArticleQuery withBrand(@Nullable String brand) {
        if (brand != null) filters.put(BRAND, a -> a.getBrand().equalsIgnoreCase(brand));
        return this;
    }

    public ArticleQuery with(ArticleQueryParam param, Article article) {
        filters.put(param, param.buildPredicate(article));
        return this;
    }
}