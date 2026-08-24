package com.seydi.pharmacie.pharmacieapi.Exception;

public class StockInsuffisantException extends RuntimeException {
    public StockInsuffisantException(String message) {
        super(message);
    }
}
