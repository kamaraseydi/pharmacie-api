package com.seydi.pharmacie.pharmacieapi.mapper;

import com.seydi.pharmacie.pharmacieapi.Model.Produit;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ProduitResponse;
import org.springframework.stereotype.Component;

@Component
public class ProduitMapper {

    //Ici on met ce qu'on ajoute dans le CreateProduit
    public Produit toEntity(CreateProduitRequest request){

        Produit produit = new Produit();

        produit.setNom(request.getNom());
        produit.setDescription(request.getDescription());
        produit.setPrix(request.getPrix());

        return produit;
    }

    //Ici on ajoute ce qu'on donne comme reponse dans le ProduitResponse
    public ProduitResponse toResponse(Produit produit){

        ProduitResponse response = new ProduitResponse();

        response.setId(produit.getId());
        response.setNom(produit.getNom());
        response.setDescription(produit.getDescription());
        response.setPrix(produit.getPrix());

        return response;
    }

    //ici ce qu'on donne lors de la modification
    public void updateEntity(Produit produit, UpdateProduitRequest request){

        //ici pas besoin de créer un nouveau produit on modifie juste les données existant

        produit.setNom(request.getNom());
        produit.setDescription(request.getDescription());
        produit.setPrix(request.getPrix());
    }


}
