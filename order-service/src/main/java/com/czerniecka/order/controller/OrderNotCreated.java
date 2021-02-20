package com.czerniecka.order.controller;

import org.springframework.http.HttpStatus;

public class OrderNotCreated extends Throwable{

    private final String message;
    private final HttpStatus httpStatus;

    public OrderNotCreated(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }


    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
