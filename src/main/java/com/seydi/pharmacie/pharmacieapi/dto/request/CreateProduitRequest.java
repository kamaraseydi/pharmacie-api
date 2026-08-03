package com.seydi.pharmacie.pharmacieapi.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateProduitRequest {

    //ici on met les champs que le produit est autorisé ou doit fournir

    @NotBlank(message = "Nom oblligatoire")
    @Size(min = 2,message = "Le nom doit contenir au moins 2 caractéres")
    private String nom;

    @NotBlank(message = "Description obligatoire")
    @Size(min = 5, max = 100,message = "Description compris entre 5 et 100 caractéres")
    private String description;

    @NotNull(message = "Prix obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
    private BigDecimal prix;

    public CreateProduitRequest(){}

    public CreateProduitRequest(String nom, String description, BigDecimal prix) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
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
        return "CreateProduitRequest{" +
                "nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                '}';
    }
}
