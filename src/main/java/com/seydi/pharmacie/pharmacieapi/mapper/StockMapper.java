package com.seydi.pharmacie.pharmacieapi.mapper;

import com.seydi.pharmacie.pharmacieapi.Model.Stock;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.StockResponse;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    // Transforme les données reçues du frontend en objet Stock.
    // Le produit n'est pas renseigné ici : il sera récupéré dans le Service.
    public Stock toEntity(CreateStockRequest request){

        Stock stock = new Stock();

        stock.setQuantite(request.getQuantite());

        return stock;
    }

    // Transforme un objet Stock en DTO à renvoyer au frontend.
    public StockResponse toResponse(Stock stock){

        StockResponse response = new StockResponse();

        response.setProduitId(stock.getProduit().getId());
        response.setNomProduit(stock.getProduit().getNom());
        response.setQuantite(stock.getQuantite());

        return response;
    }

    // Met à jour les informations modifiables d'un Stock existant.
    // Le produit sera géré dans le Service si un changement est nécessaire.
    public void updateEntity(Stock stock, UpdateStockRequest request){

        stock.setQuantite(request.getQuantite());
    }
}
