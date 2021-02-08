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
    private HttpStatus httpStatus;
    private String errorMessage;

    public PurchaseResponseDTOBuilder(Purchase purchase) {
        this.purchase = purchase;
        extras = Collections.emptyList();
        httpStatus = HttpStatus.OK;
        errorMessage = null;
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
        if (!purchase.isCanceled()) {
            receipt.setTotal(receipt.getArticles().stream().mapToDouble(PurchaseResponseArticleDTO::getCost).reduce(0, Double::sum));
        }
        return receipt;
    }

    private StatusCodeDTO buildStatusCode() {
        final StatusCodeDTO statusCode = new StatusCodeDTO();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(errorMessage != null ? errorMessage : messages.get(purchase.getStatus()));
        if (!extras.isEmpty()) {
            stringBuilder.append(" ");
            stringBuilder.append(String.join(". ", extras));
        }
        statusCode.setCode(this.httpStatus.value())
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

    public void withHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void withMessage(String message) {
        this.errorMessage = message;
    }
}
