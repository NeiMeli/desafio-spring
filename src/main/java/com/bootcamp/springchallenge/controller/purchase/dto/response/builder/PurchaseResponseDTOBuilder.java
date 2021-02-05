package com.bootcamp.springchallenge.controller.purchase.dto.response.builder;

import com.bootcamp.springchallenge.controller.purchase.dto.StatusCodeDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.PurchaseResponseDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.PurchaseResponseArticleDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.ReceiptDTO;
import com.bootcamp.springchallenge.entity.purchase.Purchase;
import com.bootcamp.springchallenge.entity.purchase.PurchaseArticle;
import com.bootcamp.springchallenge.entity.purchase.PurchaseStatus;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;

public class PurchaseResponseDTOBuilder {

    private static final Map<PurchaseStatus, String> messages;

    static {
        messages = new HashMap<>();
        messages.put(PurchaseStatus.PENDING, "La solicitud se proceso con exito");
        messages.put(PurchaseStatus.COMPLETED, "Compra completada!");
        messages.put(PurchaseStatus.CANCELED, "Compra cancelada");
    }

    public static String getMessage(PurchaseStatus status) {
        return messages.get(status);
    }

    private final Purchase purchase;
    private List<String> extras;

    public PurchaseResponseDTOBuilder(Purchase purchase) {
        this.purchase = purchase;
        extras = Collections.emptyList();
    }

    public PurchaseResponseDTO build() {
        PurchaseResponseDTO response = new PurchaseResponseDTO();
        ReceiptDTO receipt = buildReceipt();
        StatusCodeDTO statusCode = buildStatusCode();

        response.setReceipt(receipt);
        response.setStatusCode(statusCode);
        return response;
    }

    private ReceiptDTO buildReceipt() {
        ReceiptDTO receipt = new ReceiptDTO();
        receipt.setId(purchase.getId())
                .setStatus(purchase.getStatus().getValue())
                .setArticles(purchase.getArticles().stream().map(PurchaseResponseDTOBuilder::buildPurchasePurchaseResponseArticleDTO).collect(Collectors.toList()));
        return receipt;
    }

    private StatusCodeDTO buildStatusCode() {
        final StatusCodeDTO statusCode = new StatusCodeDTO();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messages.get(purchase.getStatus()));
        if (!extras.isEmpty()) {
            stringBuilder.append(String.join(". ", extras));
        }
        statusCode.setCode(HttpStatus.OK.value())
                .setMessage(stringBuilder.toString());
        return statusCode;
    }

    private static PurchaseResponseArticleDTO buildPurchasePurchaseResponseArticleDTO(PurchaseArticle purchaseArticle) {
        PurchaseResponseArticleDTO dto = new PurchaseResponseArticleDTO();
        dto.setId(purchaseArticle.getArticleId())
                .setName(purchaseArticle.getArticleName())
                .setQuantity(purchaseArticle.getQuantity())
                .setCost(purchaseArticle.calculateCost());
        return dto;
    }

    public void withExtra(String extra) {
        if (extras.isEmpty()) extras = new ArrayList<>();
        extras.add(extra);
    }
}
