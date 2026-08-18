package com.seydi.pharmacie.pharmacieapi.Model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private BigDecimal prix;

    @OneToOne(mappedBy = "produit")
    private Stock stock;

    @ManyToOne //plusieurs produits pour un seul fournisseur
    @JoinColumn(name = "fournisseur_id", nullable = false) //ceci force a la base de ne pas accepter un produit
    // sans fournisseur
    private Fournisseur fournisseur;

    public Produit(){}

    public Produit(Long id, String nom, String description, BigDecimal prix) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
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

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    //ici le getter et le setter du fournisseur


    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                '}';
    }
}
