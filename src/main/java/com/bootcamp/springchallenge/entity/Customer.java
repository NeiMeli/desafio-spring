package com.bootcamp.springchallenge.entity;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class Customer implements Persistable<String> {
    private String userName;

    public Customer(String userName) {
        this.userName = userName;
    }

    public Customer() {}

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
