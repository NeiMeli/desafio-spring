package com.bootcamp.springchallenge.controller.purchase.dto.response.builder;

public enum PurchaseResponseDTOExtra {
    BONUS_AVAILABLE("Tiene %s cupones de 5 por ciento de descuento para usar cuando quiera."),
    BONUS_CONSUMED("Se aplico el cupon de descuento de 5 por ciento a toda la compra"),
    BONUS_UNAVAILABLE("No se aplico descuento ya que no hay cupon disponible");

    private final String message;

    PurchaseResponseDTOExtra(String s) {
        this.message = s;
    }

    public String getMessage() {
        return message;
    }


    public String getMessage(Object ... args) {
        return String.format(message, args);
    }
}
