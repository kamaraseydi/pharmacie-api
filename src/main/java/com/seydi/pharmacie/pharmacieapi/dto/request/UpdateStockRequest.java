package com.seydi.pharmacie.pharmacieapi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateStockRequest {

    //ici on a pas besoin de modifier l'id du produit juste de modifier le stock si besoin

    @NotNull(message = "Quantite obligatoire")
    @Min(value = 0, message = "La quantité doit être >= 0")
    private Integer quantite;

    public UpdateStockRequest(){}

    public UpdateStockRequest(Integer quantite) {
        this.quantite = quantite;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "UpdateStockRequest{" +
                ", quantite=" + quantite +
                '}';
    }
}
