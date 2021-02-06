package com.bootcamp.springchallenge.service.impl.query;

import com.bootcamp.springchallenge.common.articlequery.ArticleQueryTestConstants;
import com.bootcamp.springchallenge.common.articlequery.QueryForTest;
import com.bootcamp.springchallenge.common.articlequery.QueryResultForTest;
import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;
import com.bootcamp.springchallenge.entity.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.bootcamp.springchallenge.service.impl.query.QueryParam.*;

@SpringBootTest
class ArticleQueryServiceImplTest {

    @Autowired
    ArticleQueryServiceImpl service;

    @Test
    void testQueryHappy() {
        QueryResultForTest qr = ArticleQueryTestConstants.QUERY_RESULT_1;
        QueryForTest query = qr.getQuery();
        List<ArticleResponseDTO> actualResults = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults)).isTrue();

        query.withMinPrestige(4);
        qr.removeResult(7);
        List<ArticleResponseDTO> actualResults2 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults2)).isTrue();

        // agrego una categoría
        query.without(NAME);
        query.withCategories(List.of(Category.TOOLS, Category.CELL_PHONES));
        qr.addResult(11);
        List<ArticleResponseDTO> actualResults3 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults3)).isTrue();

        query.without(CATEGORY);
        query.without(MIN_PRESTIGE);
        query.withName("la");
        qr.removeResult(37).removeResult(11)
                .addResult(128)
                .addResult(244);

        List<ArticleResponseDTO> actualResults4 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults4)).isTrue();

        qr.clearResults();
        query.withoutOrder()
                .without(NAME)
                .withFreeShipping(false)
                .withMinPrestige(3);
        qr.addResults(2, 21, 40, 16);
        List<ArticleResponseDTO> actualResults5 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults5)).isTrue();

        qr.clearResults();
        query.withOrder(0);
        qr.addResults(21, 16, 2, 40); // orden esperado
        List<ArticleResponseDTO> actualResults6 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults6)).isTrue();

        qr.clearResults();
        query.withMaxPrice(2300d);
        qr.addResults(21, 40); // orden esperado
        List<ArticleResponseDTO> actualResults7 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults7)).isTrue();

        qr.clearResults();
        query.without(MAX_PRICE);
        query.withStockAvailable(4);
        qr.addResults(16, 2);
        List<ArticleResponseDTO> actualResults8 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults8)).isTrue();
    }
}