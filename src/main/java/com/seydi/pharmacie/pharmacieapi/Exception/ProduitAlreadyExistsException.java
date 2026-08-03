package com.seydi.pharmacie.pharmacieapi.Exception;

public class ProduitAlreadyExistsException extends RuntimeException {
    public ProduitAlreadyExistsException(String message) {
        super(message);
    }
}
