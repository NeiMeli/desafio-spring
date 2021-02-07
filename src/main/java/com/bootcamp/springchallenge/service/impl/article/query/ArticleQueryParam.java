package com.bootcamp.springchallenge.service.impl.article.query;

import com.bootcamp.springchallenge.entity.article.Article;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum ArticleQueryParam {
    NAME("nombre", false, (a1, a2) -> a2.getName().contains(a1.getName())),
    CATEGORY ("categoria", true, (a1, a2) -> a2.getCategory() == a1.getCategory()),
    BRAND("marca", true, (a1, a2) -> a2.getBrand().equals(a1.getBrand())),
    FREE_SHIPPING("envio gratis", false, (a1, a2) -> a2.isFreeShipping() == a1.isFreeShipping()),
    MAX_PRICE("precio maximo", false, (a1, a2) -> a2.getPrice() <= a1.getPrice()),
    MIN_PRESTIGE("prestigio minimo", false, (a1, a2) -> a2.getPrestige().getValue() >= a2.getPrestige().getValue()),
    STOCK_AVAILABLE("stock disponible", false, (a1, a2) -> a2.getStock() >= a2.getStock());

    private final Matcher matcher;

    public String getLabel() {
        return label;
    }

    public static List<ArticleQueryParam> getCompatibleParams() {
        return Arrays.stream(values()).filter(ArticleQueryParam::isCompatible).collect(Collectors.toList());
    }

    private boolean isCompatible() {
        return compatible;
    }

    public Predicate<Article> buildPredicate (Article article) {
        return a -> matcher.match(article, a);
    }

    private final String label;
    private final boolean compatible;

    ArticleQueryParam(String label, boolean compatible, Matcher matcher) {
        this.label = label;
        this.compatible = compatible;
        this.matcher = matcher;
    }

    @FunctionalInterface
    public interface Matcher {
        boolean match(Article a1, Article a2);
    }

}
