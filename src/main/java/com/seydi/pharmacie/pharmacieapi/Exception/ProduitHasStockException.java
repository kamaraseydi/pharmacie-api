package com.seydi.pharmacie.pharmacieapi.exception;

public class ProduitHasStockException extends RuntimeException {
    public ProduitHasStockException(String message) {
        super(message);
    }
}
