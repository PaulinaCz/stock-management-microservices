package com.czerniecka.invoice.exceptions;

public class InvoiceNotFound extends Throwable {

    private final String message;

    public InvoiceNotFound(String message) {
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
