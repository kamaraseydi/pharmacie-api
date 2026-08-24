package com.seydi.pharmacie.pharmacieapi.mapper;

import com.seydi.pharmacie.pharmacieapi.Model.Commande;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStatutCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.CommandeResponse;
import com.seydi.pharmacie.pharmacieapi.dto.response.LigneCommandeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandeMapper {

    private final LigneCommandeMapper ligneCommandeMapper;

    public CommandeMapper(LigneCommandeMapper ligneCommandeMapper) {
        this.ligneCommandeMapper = ligneCommandeMapper;
    }

    public Commande toEntity(CreateCommandeRequest request) {

        Commande commande = new Commande();

        return commande;
    }

    public CommandeResponse toResponse(Commande commande) {

        CommandeResponse response = new CommandeResponse();

        response.setId(commande.getId());
        response.setClientId(commande.getClient().getId());
        response.setDateCommande(commande.getDateCommande());
        response.setStatut(commande.getStatut());
        response.setTotalPrix(commande.getTotalPrix());

        //Transformer les lignes
        List<LigneCommandeResponse> lignesCommandes = commande.getLigneCommandes()
                .stream()
                .map(ligneCommande -> ligneCommandeMapper.toResponse(ligneCommande))
                .toList();

        response.setLignes(lignesCommandes);

        return response;
    }

    public void updateEntity(Commande commande, UpdateStatutCommandeRequest request){

        commande.setStatut(request.getStatut());
    }
}
