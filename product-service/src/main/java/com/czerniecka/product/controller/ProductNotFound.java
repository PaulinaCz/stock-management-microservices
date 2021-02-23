package com.czerniecka.product.controller;

public class ProductNotFound extends Throwable{


    private final String message;

    public ProductNotFound(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
