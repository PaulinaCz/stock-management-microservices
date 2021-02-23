package com.czerniecka.order.exceptions;

public class ItemNotAvailable extends Throwable{

    private final String message;

    public ItemNotAvailable(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
