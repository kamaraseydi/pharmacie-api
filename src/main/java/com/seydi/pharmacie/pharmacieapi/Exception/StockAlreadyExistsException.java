package com.seydi.pharmacie.pharmacieapi.Exception;

public class StockAlreadyExistsException extends RuntimeException {
    public StockAlreadyExistsException(String message) {
        super(message);
    }
}
