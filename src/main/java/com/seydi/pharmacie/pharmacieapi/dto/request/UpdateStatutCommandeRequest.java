package com.seydi.pharmacie.pharmacieapi.dto.request;


import com.seydi.pharmacie.pharmacieapi.Model.StatutCommande;
import jakarta.validation.constraints.NotNull;

public class UpdateStatutCommandeRequest {

    //on utilise @NotBlanck pour les chaine de caracteres pour les enum on utilise @NotNull
    @NotNull(message = "Statut obligatoire")
    private StatutCommande statut;

    public UpdateStatutCommandeRequest(){}

    public UpdateStatutCommandeRequest(StatutCommande statut) {
        this.statut = statut;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "UpdateStatutCommandeRequest{" +
                "statut='" + statut + '\'' +
                '}';
    }
}
