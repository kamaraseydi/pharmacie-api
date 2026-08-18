package com.seydi.pharmacie.pharmacieapi.Exception;

public class ProduitHasStockException extends RuntimeException {
    public ProduitHasStockException(String message) {
        super(message);
    }
}
