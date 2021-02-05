package com.bootcamp.springchallenge.repository;

import com.bootcamp.springchallenge.entity.purchase.Purchase;

import java.util.Optional;

public interface PurchaseRepository {
    Optional<Purchase> findPendingPurchase(String userName);
    void persist(Purchase purchase);
    Optional<Purchase> findPendingPurchase(int purchaseId);
    Optional<Purchase> find(int receipt);
}
