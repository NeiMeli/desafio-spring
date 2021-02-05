package com.bootcamp.springchallenge.entity;

import com.bootcamp.springchallenge.exception.BadRequestException;

import java.util.Arrays;

public enum Prestige {
    ONE (1), TWO (2), THREE (3), FOUR (4), FIVE (5);

    private final int value;

    Prestige(int value) {
        this.value = value;
    }

    public static Prestige fromValue(int value) throws PrestigeNotFoundException {
        return Arrays.stream(values())
                .filter(v -> v.value == value)
                .findFirst()
                .orElseThrow(() -> new PrestigeNotFoundException(value));
    }

    public int getValue() {
        return value;
    }

    public String toPrettyString() {
        final String asterisk = "*";
        return asterisk.repeat(Math.max(0, value));
    }

    public static class PrestigeNotFoundException extends BadRequestException {
        public static final String MESSAGE = "Prestigio %s inexistente";
        public PrestigeNotFoundException(int value) {
            super(String.format(MESSAGE, value));
        }
    }
}
