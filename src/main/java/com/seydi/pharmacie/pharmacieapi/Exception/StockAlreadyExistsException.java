package com.seydi.pharmacie.pharmacieapi.exception;

public class StockAlreadyExistsException extends RuntimeException {
    public StockAlreadyExistsException(String message) {
        super(message);
    }
}
