package com.bootcamp.springchallenge.controller.purchase.dto.response;

import com.bootcamp.springchallenge.controller.purchase.dto.StatusCodeDTO;

public class PurchaseResponseDTO {
    private ReceiptDTO receipt;
    private StatusCodeDTO statusCode;

    public ReceiptDTO getReceipt() {
        return receipt;
    }

    public void setReceipt(ReceiptDTO receipt) {
        this.receipt = receipt;
    }

    public StatusCodeDTO getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCodeDTO statusCode) {
        this.statusCode = statusCode;
    }
}
