package com.bootcamp.springchallenge.controller.purchase.dto.response;

import com.bootcamp.springchallenge.controller.purchase.dto.StatusCodeDTO;

public class PurchaseResponseDTO {
    private ReceiptDTO receipt;
    private StatusCodeDTO statusCode;

    public ReceiptDTO getReceipt() {
        return receipt;
    }

    public PurchaseResponseDTO setReceipt(ReceiptDTO receipt) {
        this.receipt = receipt;
        return this;
    }

    public StatusCodeDTO getStatusCode() {
        return statusCode;
    }

    public PurchaseResponseDTO setStatusCode(StatusCodeDTO statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}
