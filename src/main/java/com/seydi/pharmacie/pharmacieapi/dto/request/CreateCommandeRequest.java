package com.seydi.pharmacie.pharmacieapi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateCommandeRequest {

    @NotNull(message = "Id du client obligatoire")
    private Long clientId;

    @NotEmpty(message = "La commande doit contenir au moins un produit")
    private List<CreateLigneCommandeRequest> lignes;

    public CreateCommandeRequest() {}

    public CreateCommandeRequest(Long clientId, List<CreateLigneCommandeRequest> lignes) {
        this.clientId = clientId;
        this.lignes = lignes;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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
                "clientId=" + clientId +
                ", lignes=" + lignes +
                '}';
    }
}
