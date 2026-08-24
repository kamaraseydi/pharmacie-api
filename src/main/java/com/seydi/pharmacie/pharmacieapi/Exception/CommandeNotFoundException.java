package com.seydi.pharmacie.pharmacieapi.Exception;

public class CommandeNotFoundException extends RuntimeException {
    public CommandeNotFoundException(String message) {
        super(message);
    }
}
