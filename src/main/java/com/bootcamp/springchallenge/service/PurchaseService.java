package com.bootcamp.springchallenge.service;

import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseClosureDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.PurchaseResponseDTO;

public interface PurchaseService {
    PurchaseResponseDTO processPurchaseRequest(PurchaseRequestDTO purchaseRequest);
    PurchaseResponseDTO confirmPurchase(PurchaseClosureDTO purchaseConfirmation);
    PurchaseResponseDTO cancelPurchase(PurchaseClosureDTO purchaseData);
}
