package com.bootcamp.springchallenge.controller.articlequery;

import com.bootcamp.springchallenge.common.CacheDBTableMock;
import com.bootcamp.springchallenge.common.article.ArticleTestBuilder;
import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;
import com.bootcamp.springchallenge.entity.article.Article;
import com.bootcamp.springchallenge.entity.article.Category;
import com.bootcamp.springchallenge.entity.article.Prestige;
import com.bootcamp.springchallenge.repository.impl.ArticleCacheRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Collectors;

import static com.bootcamp.springchallenge.common.article.ArticleForTestComparison.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleQueryControllerTest {
    @MockBean
    ArticleCacheRepository repository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    private static final String PATH = "/api/v1/articles";

    @Test // Quality práctica de clase 1 - Ejercicio 1
    void testListAll() throws Exception {
        Article a1 = new ArticleTestBuilder()
                .withName("a1")
                .withId(1)
                .withBrand("b1")
                .withPrice(100)
                .withFreeShipping(false)
                .withStock(10)
                .withCategory(Category.TOOLS)
                .withPrestige(Prestige.FOUR).build();
        Article a2 = ArticleTestBuilder.fromArticle(a1)
                .withName("a2").withId(2).withCategory(Category.SPORTS).build();
        Article a3 = ArticleTestBuilder.fromArticle(a1)
                .withName("a3").withId(3).withBrand("b2").withCategory(Category.CLOTHES).build();
        Article a4 = ArticleTestBuilder.fromArticle(a1)
                .withName("a4").withId(4).withCategory(Category.SPORTS).build();
        List<ArticleResponseDTO> expectedDtos = List.of(a1, a2, a3, a4).stream().map(Article::toDTO).collect(Collectors.toList());

        when(repository.listWhere(any())).thenCallRealMethod();
        when(repository.getDatabase()).thenReturn(new CacheDBTableMock<>(List.of(a1, a2, a3, a4)));

        MvcResult mvcResult = mvc.perform(get(PATH))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        List<ArticleResponseDTO> actualDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(toComparableStream(actualDtos)).containsAll(toComparableList(expectedDtos));
    }

    @Test // Quality práctica de clase 1 - Ejercicio 2
    void testListTwoTools() throws Exception {
        Article tool1 = new ArticleTestBuilder()
                .withName("tool1")
                .withId(1)
                .withBrand("b1")
                .withPrice(100)
                .withFreeShipping(false)
                .withStock(10)
                .withCategory(Category.TOOLS)
                .withPrestige(Prestige.FOUR).build();
        Article tool2 = ArticleTestBuilder.fromArticle(tool1)
                .withName("tool2").withId(2).build();
        Article clothe1 = ArticleTestBuilder.fromArticle(tool1)
                .withName("clothe1").withId(3).withBrand("b2").withCategory(Category.CLOTHES).build();
        Article sport1 = ArticleTestBuilder.fromArticle(tool1)
                .withName("sport1").withId(4).withCategory(Category.SPORTS).build();
        List<ArticleResponseDTO> expectedDtos = List.of(tool1, tool2).stream().map(Article::toDTO).collect(Collectors.toList());

        when(repository.listWhere(any())).thenCallRealMethod();
        when(repository.getDatabase()).thenReturn(new CacheDBTableMock<>(List.of(tool1, tool2, clothe1, sport1)));

        MvcResult mvcResult = mvc.perform(get(PATH)
                .param("category", "herramientas"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        List<ArticleResponseDTO> actualDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(toComparableStream(actualDtos)).containsAll(toComparableList(expectedDtos));
    }

    @Test // Quality práctica de clase 1 - Ejercicio 3
    void testCombineTwoFilters() throws Exception {
        Article tool1 = new ArticleTestBuilder()
                .withName("tool1")
                .withId(1)
                .withBrand("b1")
                .withPrice(100)
                .withFreeShipping(false)
                .withStock(10)
                .withCategory(Category.TOOLS)
                .withPrestige(Prestige.FOUR).build();
        Article tool2FreeShipping = ArticleTestBuilder.fromArticle(tool1)
                .withName("tool2").withId(2).withFreeShipping(true).build();
        Article tool3FreeShipping = ArticleTestBuilder.fromArticle(tool1)
                .withName("tool3").withId(3).withFreeShipping(true).build();

        Article clothe1 = ArticleTestBuilder.fromArticle(tool1)
                .withName("clothe1").withId(4).withBrand("b2").withCategory(Category.CLOTHES).build();
        Article clothe2 = ArticleTestBuilder.fromArticle(clothe1).withName("clothe2").withId(5).build();
        Article sport1 = ArticleTestBuilder.fromArticle(tool1)
                .withName("sport1").withId(6).withCategory(Category.SPORTS).withFreeShipping(true).build();
        Article sport2 = ArticleTestBuilder.fromArticle(sport1).withName("sport2").withId(7).build();
        Article cell1 = ArticleTestBuilder.fromArticle(sport1).withName("cell1").withId(8).withCategory(Category.CELL_PHONES).build();
        List<ArticleResponseDTO> expectedDtos = List.of(tool2FreeShipping, tool3FreeShipping).stream().map(Article::toDTO).collect(Collectors.toList());

        when(repository.listWhere(any())).thenCallRealMethod();
        when(repository.getDatabase()).thenReturn(new CacheDBTableMock<>(List.of(tool1, tool2FreeShipping, tool3FreeShipping, clothe1, clothe2, sport1, sport2, cell1)));

        MvcResult mvcResult = mvc.perform(get(PATH)
                .param("category", "herramientas")
                .param("freeShipping", "true"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        List<ArticleResponseDTO> actualDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(toComparableStream(actualDtos)).containsAll(toComparableList(expectedDtos));
    }

    @Test // Quality práctica de clase 1 - Ejercicio 4
    void testAlphabeticOrder() throws Exception {
        Article a1 = new ArticleTestBuilder()
                .withName("dd")
                .withId(1)
                .withBrand("b1")
                .withPrice(100)
                .withFreeShipping(false)
                .withStock(10)
                .withCategory(Category.TOOLS)
                .withPrestige(Prestige.FOUR).build();
        Article a2 = ArticleTestBuilder.fromArticle(a1)
                .withName("ac").withId(2).withCategory(Category.SPORTS).build();
        Article a3 = ArticleTestBuilder.fromArticle(a1)
                .withName("bb").withId(3).withBrand("b2").withCategory(Category.CLOTHES).build();
        Article a4 = ArticleTestBuilder.fromArticle(a1)
                .withName("ab").withId(4).withCategory(Category.SPORTS).build();

        // ascending
        List<Article> articlesWithoutOrder = List.of(a1, a2, a3, a4);
        List<Article> articlesInAscendingOrder = List.of(a4, a2, a3, a1);
        List<ArticleResponseDTO> expectedDtos = articlesInAscendingOrder.stream().map(Article::toDTO).collect(Collectors.toList());

        when(repository.listWhere(any())).thenCallRealMethod();
        when(repository.getDatabase()).thenReturn(new CacheDBTableMock<>(articlesWithoutOrder));

        MvcResult mvcResult = mvc.perform(get(PATH)
                .param("order", "0"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        List<ArticleResponseDTO> actualDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(toComparableStream(actualDtos)).containsExactly(toComparableArray(expectedDtos));

        // descending
        List<Article> articlesInDescOrder = List.of(a1, a3, a2, a4);
        List<ArticleResponseDTO> expectedDtos2 = articlesInDescOrder.stream().map(Article::toDTO).collect(Collectors.toList());

        MvcResult mvcResult2 = mvc.perform(get(PATH)
                .param("order", "1"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        List<ArticleResponseDTO> actualDtos2 = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(toComparableStream(actualDtos2)).containsExactly(toComparableArray(expectedDtos2));
    }

    @Test// Quality práctica de clase 1 - Ejercicio 5
    void testPriceAscOrder() throws Exception {
        Article a1 = new ArticleTestBuilder()
                .withName("dd")
                .withId(1)
                .withBrand("b1")
                .withPrice(100)
                .withFreeShipping(false)
                .withStock(10)
                .withCategory(Category.TOOLS)
                .withPrestige(Prestige.FOUR).build();
        Article a2 = ArticleTestBuilder.fromArticle(a1)
                .withName("ac").withId(2).withCategory(Category.SPORTS).withPrice(85).build();
        Article a3 = ArticleTestBuilder.fromArticle(a1)
                .withName("bb").withId(3).withBrand("b2").withCategory(Category.CLOTHES).withPrice(5000).build();
        Article a4 = ArticleTestBuilder.fromArticle(a1)
                .withName("ab").withId(4).withCategory(Category.SPORTS).withPrice(140).build();

        List<Article> articlesWithoutOrder = List.of(a1, a2, a3, a4);
        List<Article> articlesInAscendingOrder = List.of(a2, a1, a4, a3);
        List<ArticleResponseDTO> expectedDtos = articlesInAscendingOrder.stream().map(Article::toDTO).collect(Collectors.toList());

        when(repository.listWhere(any())).thenCallRealMethod();
        when(repository.getDatabase()).thenReturn(new CacheDBTableMock<>(articlesWithoutOrder));

        MvcResult mvcResult = mvc.perform(get(PATH)
                .param("order", "2"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        List<ArticleResponseDTO> actualDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(toComparableStream(actualDtos)).containsExactly(toComparableArray(expectedDtos));
    }

    @Test // Quality práctica de clase 1 - Ejercicio 6
    void testPriceDescOrder() throws Exception {
        Article a1 = new ArticleTestBuilder()
                .withName("dd")
                .withId(1)
                .withBrand("b1")
                .withPrice(100)
                .withFreeShipping(false)
                .withStock(10)
                .withCategory(Category.TOOLS)
                .withPrestige(Prestige.FOUR).build();
        Article a2 = ArticleTestBuilder.fromArticle(a1)
                .withName("ac").withId(2).withCategory(Category.SPORTS).withPrice(85).build();
        Article a3 = ArticleTestBuilder.fromArticle(a1)
                .withName("bb").withId(3).withBrand("b2").withCategory(Category.CLOTHES).withPrice(5000).build();
        Article a4 = ArticleTestBuilder.fromArticle(a1)
                .withName("ab").withId(4).withCategory(Category.SPORTS).withPrice(140).build();

        List<Article> articlesWithoutOrder = List.of(a1, a2, a3, a4);
        List<Article> articlesInDescOrder = List.of(a3, a4, a1, a2);
        List<ArticleResponseDTO> expectedDtos = articlesInDescOrder.stream().map(Article::toDTO).collect(Collectors.toList());

        when(repository.listWhere(any())).thenCallRealMethod();
        when(repository.getDatabase()).thenReturn(new CacheDBTableMock<>(articlesWithoutOrder));

        MvcResult mvcResult = mvc.perform(get(PATH)
                .param("order", "3"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        List<ArticleResponseDTO> actualDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        Assertions.assertThat(toComparableStream(actualDtos)).containsExactly(toComparableArray(expectedDtos));
    }
}