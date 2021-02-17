package com.bootcamp.springchallenge.entity.article;

import com.bootcamp.springchallenge.exception.BadRequestException;

import java.util.Arrays;

public enum Category {
    TOOLS("Herramientas"),
    SPORTS("Deportes"),
    CELL_PHONES("Celulares"),
    CLOTHES("Indumentaria");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public static Category fromValue(String value) throws CategoryNotFoundException {
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new CategoryNotFoundException(value));
    }

    public String getValue() {
        return value;
    }

    public static class CategoryNotFoundException extends BadRequestException {
        public static final String MESSAGE = "Categoria %s inexistente";
        public CategoryNotFoundException(String value) {
            super(String.format(MESSAGE, value));
        }
    }
}
