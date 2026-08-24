package com.seydi.pharmacie.pharmacieapi.Model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private LocalDateTime dateCommande;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    private BigDecimal totalPrix;

    //Le cascade : Quand je sauvegarde ma Commande, sauvegarde également les LigneCommande qui lui appartiennent.
    //Le CascadeType.ALL:  Une LigneCommande n'a normalement aucun sens sans sa Commande.
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<LigneCommande> ligneCommandes = new ArrayList<>();

    public Commande(){}

    public Commande(Long id, Client client, LocalDateTime dateCommande, StatutCommande statut, BigDecimal totalPrix) {
        this.id = id;
        this.client = client;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.totalPrix = totalPrix;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public BigDecimal getTotalPrix() {
        return totalPrix;
    }

    public void setTotalPrix(BigDecimal totalPrix) {
        this.totalPrix = totalPrix;
    }

    public List<LigneCommande> getLigneCommandes() {
        return ligneCommandes;
    }

    public void setLigneCommandes(List<LigneCommande> ligneCommandes) {
        this.ligneCommandes = ligneCommandes;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", client=" + client +
                ", dateCommande=" + dateCommande +
                ", statut='" + statut + '\'' +
                ", totalPrix=" + totalPrix +
                '}';
    }
}
