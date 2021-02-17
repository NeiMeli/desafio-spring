package com.bootcamp.springchallenge.common.article.query;

import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;

import static com.bootcamp.springchallenge.entity.article.Category.TOOLS;

public class ArticleQueryTestConstants {
    public static final ArticleQueryForTest QUERY_1 = (ArticleQueryForTest) new ArticleQueryForTest()
            .withName("ad").withCategories(TOOLS.getValue()).withOrder(2);

    public static final ArticleResponseDTO ARTICLE_DTO_3 = new ArticleResponseDTO(3, "Taladro", TOOLS.getValue(), 12500, "****", true);
    public static final ArticleResponseDTO ARTICLE_DTO_1 = new ArticleResponseDTO(1, "Desmalezadora", TOOLS.getValue(), 9600, "****", true);
    public static final ArticleResponseDTO ARTICLE_DTO_4 = new ArticleResponseDTO(4, "Soldadora", TOOLS.getValue(), 7215, "***", true);

    public static final QueryResultForTest QUERY_RESULT_1 = new QueryResultForTest(QUERY_1)
            .addResults( ARTICLE_DTO_4.getId(), ARTICLE_DTO_1.getId(), ARTICLE_DTO_3.getId());
}
