package com.seydi.pharmacie.pharmacieapi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateCommandeRequest {

    @NotEmpty(message = "La commande doit contenir au moins un produit")
    private List<CreateLigneCommandeRequest> lignes;

    public CreateCommandeRequest() {}

    public CreateCommandeRequest(List<CreateLigneCommandeRequest> lignes) {
        this.lignes = lignes;
    }

    public List<CreateLigneCommandeRequest> getLignes() {
        return lignes;
    }

    public void setLignes(List<CreateLigneCommandeRequest> lignes) {
        this.lignes = lignes;
    }

    @Override
    public String toString() {
        return "CreateCommandeRequest{" +
                ", lignes=" + lignes +
                '}';
    }
}
