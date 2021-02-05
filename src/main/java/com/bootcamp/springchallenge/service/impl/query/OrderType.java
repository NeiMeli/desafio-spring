package com.bootcamp.springchallenge.service.impl.query;

import com.bootcamp.springchallenge.entity.Article;
import com.bootcamp.springchallenge.exception.BadRequestException;

import java.util.Arrays;
import java.util.Comparator;

public enum OrderType {
    NONE(-1, (a1, a2) -> 0),
    ALPHABETIC_ASC(0, Comparator.comparing(Article::getName)),
    ALPHABETIC_DESC(1, Comparator.comparing(Article::getName).reversed()),
    PRICE_ASC(2, Comparator.comparing(Article::getPrice)),
    PRICE_DESC(3, Comparator.comparing(Article::getPrice).reversed());

    private final int id;
    private final Comparator<Article> comparator;

    OrderType(int id, Comparator<Article> comparator) {
        this.id = id;
        this.comparator = comparator;
    }

    public static OrderType fromId(int id) throws OrderTypeNotFoundException {
        return Arrays.stream(values())
                .filter(v -> v.id == id)
                .findFirst()
                .orElseThrow(() -> new OrderTypeNotFoundException(id));
    }

    public int getId() {
        return id;
    }

    public Comparator<Article> getComparator() {
        return comparator;
    }

    public static class OrderTypeNotFoundException extends BadRequestException {
        public static final String MESSAGE = "No existe orden con id %s";
        public OrderTypeNotFoundException(int id) {
            super(String.format(MESSAGE, id));
        }
    }
}
