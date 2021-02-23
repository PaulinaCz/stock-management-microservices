package com.czerniecka.order.exceptions;

public class OrderNotFound extends Throwable{

    private final String message;

    public OrderNotFound(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
