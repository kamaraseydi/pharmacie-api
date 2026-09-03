package com.seydi.pharmacie.pharmacieapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateCommandeRequest {

    @NotEmpty(message = "La commande doit contenir au moins un produit")
    @Schema(
            description = "Liste des produits à commander"
    )
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
