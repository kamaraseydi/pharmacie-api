package com.seydi.pharmacie.pharmacieapi.exception;

public class FournisseurNotFoundException extends RuntimeException {
    public FournisseurNotFoundException(String message) {
        super(message);
    }
}
