package com.bootcamp.springchallenge.repository.impl;

import com.bootcamp.springchallenge.entity.purchase.Purchase;
import com.bootcamp.springchallenge.repository.CacheDBTable;
import com.bootcamp.springchallenge.repository.PurchaseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Predicate;

@Repository
public class PurchaseCacheRepository implements PurchaseRepository {
    final CacheDBTable<Integer, Purchase> database;

    public PurchaseCacheRepository() throws Exception {
        this.database = new CacheDBTable<>() {
            @Override
            protected @NotNull Integer generateNextId() {
                return this.size() + 1;
            }
        };
        JsonNode jsonNodes = JsonDBUtil.parseDatabase("src/main/resources/database/purchases.json");
        for (JsonNode jsonNode : jsonNodes) {
            database.persist(Purchase.fromJson(jsonNode));
        }
    }

    @Override
    public Optional<Purchase> findPendingPurchase(String userName) {
        Predicate<Purchase> predicate = purchase -> purchase.getUserName().equals(userName) &&
                purchase.isPending();
        return database.findFirst(predicate);
    }

    @Override
    public void persist(Purchase purchase) {
        database.persist(purchase);
    }

    @Override
    public Optional<Purchase> findPendingPurchase(int purchaseId) {
        Optional<Purchase> purchase = database.find(purchaseId);
        return purchase.isPresent() && purchase.get().isPending() ? purchase : Optional.empty();
    }

    @Override
    public Optional<Purchase> find(int purchaseId) {
        return database.find(purchaseId);
    }
}
