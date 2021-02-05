package com.bootcamp.springchallenge.entity;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class Customer implements Persistable<String> {
    private String userName;
    private int amountOfPurchases;
    private int bonuses;
    private int purchasesUntilNextBonus;

    public static final int DEFAULT_PURCHASES_UNTIL_NEXT_BONUS = 3;

    public Customer(String userName) {
        this.userName = userName;
        this.amountOfPurchases = 0;
        this.bonuses = 0;
        purchasesUntilNextBonus = DEFAULT_PURCHASES_UNTIL_NEXT_BONUS;
    }

    public Customer() {
        this.amountOfPurchases = 0;
        this.bonuses = 0;
        purchasesUntilNextBonus = DEFAULT_PURCHASES_UNTIL_NEXT_BONUS;
    }

    public static Customer fromJson(JsonNode jn) {
        Customer customer = new Customer();
        customer.setUserName(jn.get("userName").textValue());
        return customer;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean hasBonusAvailable() {
        return bonuses > 0;
    }

    public void consumeBonus() {
        bonuses --;
    }

    public void addPurchase() {
        amountOfPurchases ++;
        if (amountOfPurchases == purchasesUntilNextBonus) {
            addBonus();
        }
    }

    private void addBonus() {
        bonuses ++;
        purchasesUntilNextBonus += DEFAULT_PURCHASES_UNTIL_NEXT_BONUS;
    }

    @Override
    public String getPrimaryKey() {
        return userName;
    }

    @Override
    public boolean isNew() {
        return Strings.isBlank(getPrimaryKey());
    }

    @Override
    public void setId(@NotNull String id) {
        setUserName(id);
    }
}
