package com.czerniecka.customer.exceptions;

public class CustomerNotFound extends Throwable{

    private final String message;

    public CustomerNotFound(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
