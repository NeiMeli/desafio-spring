package com.bootcamp.springchallenge.controller.purchase.dto.response.builder;

public enum PurchaseResponseDTOExtra {
    BONUS_AVAILABLE("Tiene 5% de descuento para usar cuando quiera."),
    BONUS_CONSUMED("Se aplico el descuento de 5% a toda la compra"),
    BONUS_UNAVAILABLE("No se aplico descuento ya que no hay descuento disponible");

    private final String message;

    PurchaseResponseDTOExtra(String s) {
        this.message = s;
    }

    public String getMessage() {
        return message;
    }
}
