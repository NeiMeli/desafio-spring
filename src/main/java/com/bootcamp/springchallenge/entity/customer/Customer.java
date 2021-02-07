package com.bootcamp.springchallenge.entity.customer;

import com.bootcamp.springchallenge.entity.Persistable;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class Customer implements Persistable<String> {
    private String userName;
    private Province province;
    private int bonuses;
    private int purchasesUntilNextBonus;
    public static final int DEFAULT_PURCHASES_UNTIL_NEXT_BONUS = 3;

    public Customer(String userName) {
        this(userName, Province.UNDEFINED);
    }

    public Customer(String userName, Province province) {
        this.userName = userName;
        this.bonuses = 0;
        this.province = province;
        purchasesUntilNextBonus = DEFAULT_PURCHASES_UNTIL_NEXT_BONUS;
    }

    public Customer() {
        this("", Province.UNDEFINED);
    }

    public static Customer fromJson(JsonNode jn) {
        Customer customer = new Customer();
        customer.setUserName(jn.get("userName").textValue());
        customer.setProvince(Province.fromLabel(jn.get("province").textValue()));
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

    public void registerPurchase() {
        purchasesUntilNextBonus --;
        if (purchasesUntilNextBonus == 0) {
            addBonus();
            purchasesUntilNextBonus = DEFAULT_PURCHASES_UNTIL_NEXT_BONUS;
        }
    }

    private void addBonus() {
        bonuses ++;
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

    public int getBonuses() {
        return bonuses;
    }

    public Province getProvince() {
        return province;
    }

    public Customer setProvince(Province province) {
        this.province = province;
        return this;
    }
}
