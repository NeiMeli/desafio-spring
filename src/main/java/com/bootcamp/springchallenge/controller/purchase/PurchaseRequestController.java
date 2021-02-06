package com.bootcamp.springchallenge.controller.purchase;

import com.bootcamp.springchallenge.controller.ExceptionHandlerController;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseClosureDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.PurchaseResponseDTO;
import com.bootcamp.springchallenge.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PurchaseRequestController extends ExceptionHandlerController {
    @Autowired
    PurchaseService service;

    @PostMapping("/purchase-request")
    public PurchaseResponseDTO purchaseRequest(@RequestBody PurchaseRequestDTO purchaseRequest) {
        return service.processPurchaseRequest(purchaseRequest);
    }

    @PostMapping("/purchase-confirmation")
    public PurchaseResponseDTO purchaseConfirmation(@RequestBody PurchaseClosureDTO purchaseData) {
        return service.confirmPurchase(purchaseData);
    }

    @PostMapping("/purchase-cancelation")
    public PurchaseResponseDTO purchaseCancelation(@RequestBody PurchaseClosureDTO purchaseData) {
        return service.cancelPurchase(purchaseData);
    }
}
