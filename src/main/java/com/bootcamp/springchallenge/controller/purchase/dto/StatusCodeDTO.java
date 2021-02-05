package com.bootcamp.springchallenge.controller.purchase.dto;

public class StatusCodeDTO {
    private int code;
    private String message;

    public StatusCodeDTO() {
    }

    public int getCode() {
        return code;
    }

    public StatusCodeDTO setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public StatusCodeDTO setMessage(String message) {
        this.message = message;
        return this;
    }
}
