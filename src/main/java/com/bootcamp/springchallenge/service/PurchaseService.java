package com.bootcamp.springchallenge.service;

import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseDataDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.PurchaseResponseDTO;

public interface PurchaseService {
    PurchaseResponseDTO processPurchaseRequest(PurchaseRequestDTO purchaseRequest);
    PurchaseResponseDTO confirmPurchase(PurchaseDataDTO purchaseConfirmation);
    PurchaseResponseDTO cancelPurchase(PurchaseDataDTO purchaseData);
}
