package com.seydi.pharmacie.pharmacieapi.dto.response;

import com.seydi.pharmacie.pharmacieapi.model.StatutCommande;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CommandeResponse {

    private Long id;
    private Long clientId;
    private LocalDateTime dateCommande;
    private StatutCommande statut;
    private BigDecimal totalPrix;
    private List<LigneCommandeResponse> lignes;

    public CommandeResponse() {}

    public CommandeResponse(Long id, Long clientId, LocalDateTime dateCommande, StatutCommande statut, BigDecimal totalPrix, List<LigneCommandeResponse> lignes) {
        this.id = id;
        this.clientId = clientId;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.totalPrix = totalPrix;
        this.lignes = lignes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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

    public List<LigneCommandeResponse> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommandeResponse> lignes) {
        this.lignes = lignes;
    }

    @Override
    public String toString() {
        return "CommandeResponse{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", dateCommande=" + dateCommande +
                ", statut=" + statut +
                ", totalPrix=" + totalPrix +
                ", lignes=" + lignes +
                '}';
    }
}
