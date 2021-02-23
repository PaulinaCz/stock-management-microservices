package com.czerniecka.inventory.exceptions;

public class InventoryNotFound extends Throwable{

    private final String message;

    public InventoryNotFound(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
