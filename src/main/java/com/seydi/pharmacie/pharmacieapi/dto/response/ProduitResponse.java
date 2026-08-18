package com.seydi.pharmacie.pharmacieapi.dto.response;

import java.math.BigDecimal;

public class ProduitResponse {

    //Un Response DTO contient uniquement les informations que le serveur souhaite renvoyer au client.

    private Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private Long fournisseurId;
    private String nomFournisseur;

    public ProduitResponse(){}

    public ProduitResponse(Long id, String nom, String description, BigDecimal prix, Long fournisseurId,String nomFournisseur) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.fournisseurId  = fournisseurId;
        this.nomFournisseur = nomFournisseur;
    }

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public String getNomFournisseur() {
        return nomFournisseur;
    }

    public void setNomFournisseur(String nomFournisseur) {
        this.nomFournisseur = nomFournisseur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "ProduitResponse{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                '}';
    }
}
