package com.seydi.pharmacie.pharmacieapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateLigneCommandeRequest {

    @NotNull(message = "Id du produit obligatoire")
    @Schema(
            description = "Identifiant du produit à commander",
            example = "1"
    )
    private Long produitId;

    @NotNull(message = "Quantite obligatoire")
    @Min(value = 1, message = "La quantité doit être >= 1")
    @Schema(
            description = "Quantité souhaitée", example = "2",
            minimum = "1"
    )
    private Integer quantite;

    public CreateLigneCommandeRequest(){}

    public CreateLigneCommandeRequest(Long produitId, Integer quantite) {
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
        return "CreateLigneCommandeRequest{" +
                "produitId=" + produitId +
                ", quantite=" + quantite +
                '}';
    }
}
