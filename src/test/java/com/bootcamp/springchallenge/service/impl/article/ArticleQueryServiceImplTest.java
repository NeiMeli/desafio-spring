package com.bootcamp.springchallenge.service.impl.article;

import com.bootcamp.springchallenge.common.articlequery.ArticleQueryTestConstants;
import com.bootcamp.springchallenge.common.articlequery.ArticleQueryForTest;
import com.bootcamp.springchallenge.common.articlequery.QueryResultForTest;
import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;
import com.bootcamp.springchallenge.entity.article.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.bootcamp.springchallenge.service.impl.article.query.ArticleQueryParam.*;

@SpringBootTest
class ArticleQueryServiceImplTest {

    @Autowired
    ArticleQueryServiceImpl service;

    @Test
    void testQueryHappy() {
        QueryResultForTest qr = ArticleQueryTestConstants.QUERY_RESULT_1;
        ArticleQueryForTest query = qr.getQuery();
        List<ArticleResponseDTO> actualResults = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults)).isTrue();

        query.withMinPrestige(4);
        qr.removeResult(4);
        List<ArticleResponseDTO> actualResults2 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults2)).isTrue();

        // agrego una categoría
        query.without(NAME);
        query.withCategories(List.of(Category.TOOLS, Category.CELL_PHONES));
        qr.addResult(8);
        List<ArticleResponseDTO> actualResults3 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults3)).isTrue();

        query.without(CATEGORY);
        query.without(MIN_PRESTIGE);
        query.withName("la");
        qr.removeResult(1).removeResult(8)
                .addResult(6)
                .addResult(5);

        List<ArticleResponseDTO> actualResults4 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults4)).isTrue();

        qr.clearResults();
        query.withoutOrder()
                .without(NAME)
                .withFreeShipping(false)
                .withMinPrestige(3);
        qr.addResults(2, 7, 10, 11);
        List<ArticleResponseDTO> actualResults5 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults5)).isTrue();

        qr.clearResults();
        query.withOrder(0);
        qr.addResults(7, 11, 2, 10); // orden esperado
        List<ArticleResponseDTO> actualResults6 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults6)).isTrue();

        qr.clearResults();
        query.withMaxPrice(2300d);
        qr.addResults(7, 10); // orden esperado
        List<ArticleResponseDTO> actualResults7 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults7)).isTrue();

        qr.clearResults();
        query.without(MAX_PRICE);
        query.withStockAvailable(4);
        qr.addResults(11, 2);
        List<ArticleResponseDTO> actualResults8 = service.query(query);
        Assertions.assertThat(qr.hasResult(actualResults8)).isTrue();
    }
}