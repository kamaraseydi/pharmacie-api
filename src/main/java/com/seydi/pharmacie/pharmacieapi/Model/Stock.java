package com.seydi.pharmacie.pharmacieapi.Model;

import jakarta.persistence.*;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantite;

    //Nous utilisons celui ci parce que pour l'instant notre appli nous avons un seul pharmacie
    //et celle ci n'a qu'un seul stock pour un produit si demain nous avons plusieus pharmacie nous pourons alors
//faire @ManyToOne car ici un produit peut avoir plusieurs stock selon le pharmacie et ici nous maitrions @ManyToOne

    @OneToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    public Stock(){}

    public Stock(Long id, Integer quantite, Produit produit) {
        this.id = id;
        this.quantite = quantite;
        this.produit = produit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantite() {
        return this.quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", quantite=" + quantite +
                ", produit=" + produit +
                '}';
    }
}
