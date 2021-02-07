package com.bootcamp.springchallenge.common.purchase;

import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestArticleDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestDTO;

import java.util.List;
import java.util.function.Supplier;

import static com.bootcamp.springchallenge.common.customer.CustomerTestConstants.CUSTOMER_1;
import static com.bootcamp.springchallenge.common.customer.CustomerTestConstants.CUSTOMER_2;

public class PurchaseTestConstants {
    public static final Supplier<PurchaseRequestArticleDTO> PR_ARTICLE_DTO_12 = () -> new PurchaseRequestArticleDTO()
            .setArticleId(12).setDiscount(40).setQuantity(2);
    public static final Supplier<PurchaseRequestArticleDTO> PR_ARTICLE_DTO_13 = () -> new PurchaseRequestArticleDTO()
            .setArticleId(13).setQuantity(1);
    public static final Supplier<PurchaseRequestArticleDTO> PR_ARTICLE_DTO_4 = () -> new PurchaseRequestArticleDTO()
            .setArticleId(4).setDiscount(30).setQuantity(1);
    public static final Supplier<PurchaseRequestArticleDTO> PR_ARTICLE_DTO_4_2 = () -> new PurchaseRequestArticleDTO()
            .setArticleId(4).setDiscount(10).setQuantity(1);
    public static final Supplier<PurchaseRequestDTO> PURCHASE_REQUEST_DTO1 = () -> new PurchaseRequestDTO()
            .setUserName(CUSTOMER_1.getUserName())
            .setArticles(List.of(PR_ARTICLE_DTO_12.get(), PR_ARTICLE_DTO_13.get(), PR_ARTICLE_DTO_4.get()));
    public static final Supplier<PurchaseRequestDTO> PURCHASE_REQUEST_DTO2 = () -> new PurchaseRequestDTO()
            .setUserName(CUSTOMER_2.getUserName())
            .setArticles(List.of(PR_ARTICLE_DTO_13.get()));
}
