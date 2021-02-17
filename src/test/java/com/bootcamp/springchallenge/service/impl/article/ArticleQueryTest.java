package com.bootcamp.springchallenge.service.impl.article;

import com.bootcamp.springchallenge.common.article.query.ArticleQueryForTest;
import com.bootcamp.springchallenge.entity.article.Category;
import com.bootcamp.springchallenge.entity.article.Prestige;
import com.bootcamp.springchallenge.service.impl.article.query.OrderType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ArticleQueryTest {
    @Test
    void testBadRequests() {
        ArticleQueryForTest query = new ArticleQueryForTest();
        Assertions.assertThatExceptionOfType(Prestige.PrestigeNotFoundException.class)
                .isThrownBy(() -> query.withMinPrestige(8))
                .withMessageContaining(String.format(Prestige.PrestigeNotFoundException.MESSAGE, 8));
        query.clear();

        Assertions.assertThatExceptionOfType(Category.CategoryNotFoundException.class)
                .isThrownBy(() -> query.withCategories("non-existant-category"))
                .withMessageContaining(String.format(Category.CategoryNotFoundException.MESSAGE, "non-existant-category"));
        query.clear();

        int invalidOrder = OrderType.values().length;
        Assertions.assertThatExceptionOfType(OrderType.OrderTypeNotFoundException.class)
                .isThrownBy(() -> query.withOrder(invalidOrder))
                .withMessageContaining(String.format(OrderType.OrderTypeNotFoundException.MESSAGE, invalidOrder));
    }
}