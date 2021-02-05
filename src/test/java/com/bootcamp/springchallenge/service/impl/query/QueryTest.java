package com.bootcamp.springchallenge.service.impl.query;

import com.bootcamp.springchallenge.common.articlequery.QueryForTest;
import com.bootcamp.springchallenge.entity.Category;
import com.bootcamp.springchallenge.entity.Prestige;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class QueryTest {
    @Test
    void testBadRequests() {
        QueryForTest query = new QueryForTest();
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