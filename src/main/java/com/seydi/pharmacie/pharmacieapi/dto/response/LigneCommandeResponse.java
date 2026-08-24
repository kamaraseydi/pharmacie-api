package com.seydi.pharmacie.pharmacieapi.dto.response;

import java.math.BigDecimal;

public class LigneCommandeResponse {

    private Long produitId;
    private String nomProduit;
    private Integer quantite;
    private BigDecimal prixUnitaire;

    public LigneCommandeResponse() {}

    public LigneCommandeResponse(Long produitId, String nomProduit, Integer quantite, BigDecimal prixUnitaire) {
        this.produitId = produitId;
        this.nomProduit = nomProduit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    @Override
    public String toString() {
        return "LigneCommandeResponse{" +
                "produitId=" + produitId +
                ", nomProduit='" + nomProduit + '\'' +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                '}';
    }
}
