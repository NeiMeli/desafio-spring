package com.bootcamp.springchallenge.service.impl.query;

import com.bootcamp.springchallenge.entity.Article;
import com.bootcamp.springchallenge.entity.Category;
import com.bootcamp.springchallenge.entity.Prestige;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Query {
    protected final Map<QueryParam, Predicate<Article>> filters;
    protected @NotNull OrderType orderType;

    public Query() {
        filters = new HashMap<>();
        orderType = OrderType.NONE;
    }

    public Query withName(@Nullable String name) {
        if (name != null) filters.put(QueryParam.NAME, p -> p.getName().contains(name));
        return this;
    }

    public Query withCategories(@Nullable String... categories) {
        List<Category> categoryList = Arrays.stream(categories).map(Category::fromValue).collect(Collectors.toList());
        return withCategories(categoryList);
    }

    public Query withCategories(List<Category> categories) {
        if (categories.size() > 0) {
            filters.put(QueryParam.CATEGORY, a -> categories.contains(a.getCategory()));
        }
        return this;
    }

    public Query withMaxPrice(@Nullable Double maxPrice) {
        if (maxPrice != null) filters.put(QueryParam.MAX_PRICE, p -> Double.compare(p.getPrice(), maxPrice) <= 0);
        return this;
    }

    public Query withFreeShipping(@Nullable Boolean freeShipping) {
        if (freeShipping != null) filters.put(QueryParam.FREE_SHIPPING, p -> p.isFreeShipping() == freeShipping);
        return this;
    }

    public Query withMinPrestige(@Nullable Integer minPrestige) {
        if (minPrestige != null) {
            Prestige prest = Prestige.fromValue(minPrestige); // lo busco para validar
            filters.put(QueryParam.MIN_PRESTIGE, p -> p.getPrestige().getValue() >= prest.getValue());
        }
        return this;
    }

    public Query withOrder(@Nullable Integer order) {
        if (order != null) {
            orderType = OrderType.fromId(order);
        } else {
            orderType = OrderType.NONE;
        }
        return this;
    }

    public Predicate<Article> buildPredicate() {
        return article -> filters.values().stream().allMatch(p -> p.test(article));
    }

    public Comparator<Article> getComparator() {
        return orderType.getComparator();
    }
}