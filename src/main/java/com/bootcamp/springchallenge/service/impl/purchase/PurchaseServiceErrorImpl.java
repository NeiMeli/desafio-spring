package com.bootcamp.springchallenge.service.impl.purchase;

public enum PurchaseServiceErrorImpl implements PurchaseServiceError {
    EMPTY_ARTICLE_LIST("Lista de articulos vacia"),
    ARTICLE_NOT_FOUND("Articulo %s no encontrado"),
    NOT_ENOUGH_STOCK("No hay stock suficiente de %s"),
    NOT_ENOUGH_STOCK_SUGGESTION("No hay stock suficiente de %s. %s"),
    INVALID_DISCOUNT_VALUE("Valor de descuento invalido %s"),
    EMPTY_USERNAME("Nombre de usuario vacio"),
    INVALID_ARTICLE_ID("Identificador de articulo invalido %s"),
    INVALID_QUANTITY("Cantidad invalida %s"),
    PURCHASE_NOT_FOUND("Orden de compra no encontrada"),
    INVALID_PURCHASE_ID("Numero de compra invalido %s");

    private final String message;

    PurchaseServiceErrorImpl(String s) {
        this.message = s;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessage(Object ... args) {
        return String.format(message, args);
    }
}
