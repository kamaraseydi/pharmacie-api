package com.seydi.pharmacie.pharmacieapi.exception;

public class FournisseurAlreadyExistsException extends RuntimeException {
    public FournisseurAlreadyExistsException(String message) {
        super(message);
    }
}
