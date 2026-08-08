package com.seydi.pharmacie.pharmacieapi.dto.response;

public class StockResponse {

    private Long produitId;
    private String nomProduit;
    private Integer quantite;

    public StockResponse(){}

    public StockResponse(Long produitId, String nomProduit, Integer quantite) {
        this.produitId = produitId;
        this.nomProduit = nomProduit;
        this.quantite = quantite;
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

    @Override
    public String toString() {
        return "StockResponse{" +
                "produitId=" + produitId +
                ", nomProduit='" + nomProduit + '\'' +
                ", quantite=" + quantite +
                '}';
    }
}
