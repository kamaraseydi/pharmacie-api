package com.seydi.pharmacie.pharmacieapi.mapper;

import com.seydi.pharmacie.pharmacieapi.Model.Fournisseur;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.FournisseurResponse;
import org.springframework.stereotype.Component;

@Component
public class FournisseurMapper {

    public Fournisseur toEntity(CreateFournisseurRequest request){

        Fournisseur fournisseur = new Fournisseur();

        fournisseur.setNom(request.getNom());
        fournisseur.setAdresse(request.getAdresse());
        fournisseur.setTelephone(request.getTelephone());
        fournisseur.setEmail(request.getEmail());

        return fournisseur;
    }

    public FournisseurResponse toResponse(Fournisseur fournisseur){

        FournisseurResponse response = new FournisseurResponse();

        response.setId(fournisseur.getId());
        response.setNom(fournisseur.getNom());
        response.setAdresse(fournisseur.getAdresse());
        response.setTelephone(fournisseur.getTelephone());
        response.setEmail(fournisseur.getEmail());

        return response;
    }

    public void updateEntity(Fournisseur fournisseur, UpdateFournisseurRequest request){

        fournisseur.setNom(request.getNom());
        fournisseur.setAdresse(request.getAdresse());
        fournisseur.setTelephone(request.getTelephone());
        fournisseur.setEmail(request.getEmail());
    }
}
