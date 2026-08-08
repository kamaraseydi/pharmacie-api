package com.seydi.pharmacie.pharmacieapi.Exception;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String message) {
        super(message);
    }
}
