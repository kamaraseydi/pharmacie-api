package com.seydi.pharmacie.pharmacieapi.Exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }
}
