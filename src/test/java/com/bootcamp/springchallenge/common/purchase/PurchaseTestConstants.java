package com.bootcamp.springchallenge.common.purchase;

import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestArticleDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestDTO;

import java.util.List;
import java.util.function.Supplier;

import static com.bootcamp.springchallenge.common.customer.CustomerTestConstants.CUSTOMER_1;
import static com.bootcamp.springchallenge.common.customer.CustomerTestConstants.CUSTOMER_2;

public class PurchaseTestConstants {
    public static final Supplier<PurchaseRequestArticleDTO> PR_ARTICLE_DTO_181 = () -> new PurchaseRequestArticleDTO()
            .setArticleId(181).setDiscount(40).setQuantity(2);
    public static final Supplier<PurchaseRequestArticleDTO> PR_ARTICLE_DTO_92 = () -> new PurchaseRequestArticleDTO()
            .setArticleId(92).setQuantity(1);
    public static final Supplier<PurchaseRequestArticleDTO> PR_ARTICLE_DTO_7 = () -> new PurchaseRequestArticleDTO()
            .setArticleId(7).setDiscount(30).setQuantity(1);
    public static final Supplier<PurchaseRequestArticleDTO> PR_ARTICLE_DTO_7_2 = () -> new PurchaseRequestArticleDTO()
            .setArticleId(7).setDiscount(10).setQuantity(1);
    public static final Supplier<PurchaseRequestDTO> PURCHASE_REQUEST_DTO1 = () -> new PurchaseRequestDTO()
            .setUserName(CUSTOMER_1.getUserName())
            .setArticles(List.of(PR_ARTICLE_DTO_181.get(), PR_ARTICLE_DTO_92.get(), PR_ARTICLE_DTO_7.get()));

    public static final Supplier<PurchaseRequestDTO> PURCHASE_REQUEST_DTO2 = () -> new PurchaseRequestDTO()
            .setUserName(CUSTOMER_2.getUserName())
            .setArticles(List.of(PR_ARTICLE_DTO_92.get()));
}
