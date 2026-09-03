package com.seydi.pharmacie.pharmacieapi.exception;

public class ProduitDejaDansCommandeException extends RuntimeException {
    public ProduitDejaDansCommandeException(String message) {
        super(message);
    }
}
