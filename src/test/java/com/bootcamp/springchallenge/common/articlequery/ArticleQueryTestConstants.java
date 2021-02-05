package com.bootcamp.springchallenge.common.articlequery;

import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;

import static com.bootcamp.springchallenge.entity.Category.TOOLS;

public class ArticleQueryTestConstants {
    public static final QueryForTest QUERY_1 = (QueryForTest) new QueryForTest()
            .withName("ad").withCategories(TOOLS.getValue()).withOrder(2);

    public static final ArticleResponseDTO ARTICLE_DTO_12 = new ArticleResponseDTO(12, "Taladro", TOOLS.getValue(), 12500, "****", true);
    public static final ArticleResponseDTO ARTICLE_DTO_37 = new ArticleResponseDTO(37, "Desmalezadora", TOOLS.getValue(), 9600, "****", true);
    public static final ArticleResponseDTO ARTICLE_DTO_7 = new ArticleResponseDTO(7, "Soldadora", TOOLS.getValue(), 7215, "***", true);

    public static final QueryResultForTest QUERY_RESULT_1 = new QueryResultForTest(QUERY_1)
            .addResults( ARTICLE_DTO_7.getId(), ARTICLE_DTO_37.getId(), ARTICLE_DTO_12.getId());
}
