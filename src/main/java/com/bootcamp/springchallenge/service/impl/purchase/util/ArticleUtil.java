package com.bootcamp.springchallenge.service.impl.purchase.util;

import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestArticleDTO;
import com.bootcamp.springchallenge.entity.purchase.PurchaseArticle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ArticleUtil {
    public static Map<Integer, Integer> getQuantitiesByArticleDtoId(List<PurchaseRequestArticleDTO> articles) {
        return getQuantitiesByArticleIdMap(articles, PurchaseRequestArticleDTO::getArticleId, PurchaseRequestArticleDTO::getQuantity);
    }

    public static Map<Integer, Integer> getQuantitiesByArticleId(List<PurchaseArticle> articles) {
        return getQuantitiesByArticleIdMap(articles, PurchaseArticle::getArticleId, PurchaseArticle::getQuantity);
    }

    private static <T> Map<Integer, Integer> getQuantitiesByArticleIdMap(List<T> articles, Function<T, Integer> idGetter, Function<T, Integer> quantityGetter) {
        final Map<Integer, Integer> quantityByArticleId = new HashMap<>();
        articles.forEach(article -> quantityByArticleId.merge(idGetter.apply(article), quantityGetter.apply(article), Integer::sum));
        return quantityByArticleId;
    }
}
