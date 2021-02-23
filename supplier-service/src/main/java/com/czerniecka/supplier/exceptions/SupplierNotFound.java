package com.czerniecka.supplier.exceptions;

public class SupplierNotFound extends Throwable{

    private final String message;

    public SupplierNotFound(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
