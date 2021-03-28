package com.bootcamp.springchallenge.controller.purchase;

import com.bootcamp.springchallenge.common.CacheDBTableMock;
import com.bootcamp.springchallenge.common.article.ArticleTestBuilder;
import com.bootcamp.springchallenge.controller.common.util.DoubleProcessor;
import com.bootcamp.springchallenge.controller.purchase.dto.StatusCodeDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestArticleDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.PurchaseResponseDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.ReceiptDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.builder.PurchaseResponseDTOBuilder;
import com.bootcamp.springchallenge.entity.article.Article;
import com.bootcamp.springchallenge.entity.article.Category;
import com.bootcamp.springchallenge.entity.article.Prestige;
import com.bootcamp.springchallenge.entity.customer.Customer;
import com.bootcamp.springchallenge.entity.customer.Province;
import com.bootcamp.springchallenge.entity.purchase.PurchaseStatus;
import com.bootcamp.springchallenge.exception.BadRequestException;
import com.bootcamp.springchallenge.repository.impl.ArticleCacheRepository;
import com.bootcamp.springchallenge.repository.impl.CustomerCacheRepository;
import com.bootcamp.springchallenge.repository.impl.PurchaseCacheRepository;
import com.bootcamp.springchallenge.service.impl.purchase.exception.PurchaseServiceErrorImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PurchaseRequestControllerTest {
    @MockBean
    ArticleCacheRepository articleRepository;

    @MockBean
    CustomerCacheRepository customerRepository;

    @MockBean
    PurchaseCacheRepository purchaseRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    private static final String PATH = "/api/v1/purchase-request";
    public static final Function<PurchaseRequestDTO, String> JSON_GENERATOR = JSONValue::toJSONString;

    @Test // Quality práctica de clase 1 - Ejercicio 7
    void testPurchaseOrderComplete() throws Exception {
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
                .withName("a2").withId(2).withPrice(200).withCategory(Category.SPORTS).build();
        Article a3 = ArticleTestBuilder.fromArticle(a1)
                .withName("a3").withId(3).withBrand("b2").withCategory(Category.CLOTHES).build();
        Article a4 = ArticleTestBuilder.fromArticle(a1)
                .withName("a4").withId(4).withCategory(Category.SPORTS).build();

        Customer customer = new Customer("customer1", Province.BUENOS_AIRES);
        when(customerRepository.getDatabase()).thenReturn(new CacheDBTableMock<>(List.of(customer)));
        when(customerRepository.find(anyString())).thenCallRealMethod();
        when(articleRepository.getDatabase()).thenReturn(new CacheDBTableMock<>(List.of(a1, a2, a3, a4)));
        when(articleRepository.find(anyInt())).thenCallRealMethod();
        when(purchaseRepository.find(anyInt())).thenReturn(Optional.empty());

        // happy path
        PurchaseRequestDTO validRequest = new PurchaseRequestDTO();
        validRequest.setUserName(customer.getUserName());
        List<Article> selectedArticles = List.of(a1, a2);
        List<PurchaseRequestArticleDTO> validArticleDTOs = selectedArticles.stream().map(a -> new PurchaseRequestArticleDTO().setArticleId(a.getId()).setQuantity(1).setDiscount(10)).collect(Collectors.toList());
        validRequest.setArticles(validArticleDTOs);

        MvcResult mvcResult1 = mvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON).content(JSON_GENERATOR.apply(validRequest)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        PurchaseResponseDTO actualResponse = objectMapper.readValue(mvcResult1.getResponse().getContentAsString(), PurchaseResponseDTO.class);
        final double expectedTotalPrice = DoubleProcessor.roundTwoDecimals((a1.getPrice() + a2.getPrice()) * 0.9d); // ambos tienen qty 1 y 10% de desc
        ReceiptDTO receipt = actualResponse.getReceipt();
        assertThat(receipt.getTotal()).isEqualTo(expectedTotalPrice);
        assertThat(receipt.getArticles()).hasSize(2);
        List<Integer> selectedArticlesId = selectedArticles.stream().map(Article::getId).collect(Collectors.toList());
        assertThat(receipt.getArticles().stream()).allMatch(a -> selectedArticlesId.contains(a.getId()));
        assertThat(receipt.getStatus()).isEqualTo(PurchaseStatus.PENDING.getValue());
        StatusCodeDTO statusCode = actualResponse.getStatusCode();
        assertThat(statusCode.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(statusCode.getMessage()).isEqualTo(PurchaseResponseDTOBuilder.getMessage(PurchaseStatus.PENDING));

        // bad request, article not found
        PurchaseRequestDTO invalidRequest = new PurchaseRequestDTO();
        invalidRequest.setUserName(customer.getUserName());
        int nonExistentArticleId = Integer.MAX_VALUE;
        List<PurchaseRequestArticleDTO> invalidArticleDTOs = List.of(new PurchaseRequestArticleDTO().setArticleId(a1.getId()).setQuantity(2), new PurchaseRequestArticleDTO().setArticleId(nonExistentArticleId).setQuantity(1).setDiscount(10));
        invalidRequest.setArticles(invalidArticleDTOs);

        MvcResult mvcResult2 = mvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON).content(JSON_GENERATOR.apply(invalidRequest)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andReturn();
        assertThat(mvcResult2.getResolvedException())
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(PurchaseServiceErrorImpl.ARTICLE_NOT_FOUND.getMessage(nonExistentArticleId));
    }

}