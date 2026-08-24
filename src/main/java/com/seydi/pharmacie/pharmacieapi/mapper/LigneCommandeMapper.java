package com.seydi.pharmacie.pharmacieapi.mapper;

import com.seydi.pharmacie.pharmacieapi.Model.LigneCommande;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateLigneCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.LigneCommandeResponse;
import org.springframework.stereotype.Component;

@Component
public class LigneCommandeMapper {

    public LigneCommande toEntity(CreateLigneCommandeRequest request){

        LigneCommande ligneCommande = new LigneCommande();

        ligneCommande.setQuantite(request.getQuantite());

       return ligneCommande;
    }

    public LigneCommandeResponse toResponse(LigneCommande ligneCommande){

        LigneCommandeResponse response = new LigneCommandeResponse();

        response.setProduitId(ligneCommande.getProduit().getId());
        response.setNomProduit(ligneCommande.getProduit().getNom());
        response.setQuantite(ligneCommande.getQuantite());
        response.setPrixUnitaire(ligneCommande.getPrixUnitaire());

        return response;
    }


}
