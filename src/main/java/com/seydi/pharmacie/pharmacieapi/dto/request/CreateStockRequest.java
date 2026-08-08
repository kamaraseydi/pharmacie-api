package com.seydi.pharmacie.pharmacieapi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateStockRequest {

    @NotNull(message = "Id du produit obligatoire")
    private Long produitId;

    @NotNull(message = "Quantite obligatoire")
    @Min(value = 0, message = "La quantité doit être >= 0")
    private Integer quantite;

    public CreateStockRequest(){}

    public CreateStockRequest(Long produitId, Integer quantite) {
        this.produitId = produitId;
        this.quantite = quantite;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "CreateStockRequest{" +
                "produitId=" + produitId +
                ", quantite=" + quantite +
                '}';
    }
}
