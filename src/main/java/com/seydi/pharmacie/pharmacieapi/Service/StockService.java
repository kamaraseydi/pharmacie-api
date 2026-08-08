package com.seydi.pharmacie.pharmacieapi.Service;

import com.seydi.pharmacie.pharmacieapi.Exception.ProduitNotFoundException;
import com.seydi.pharmacie.pharmacieapi.Exception.StockAlreadyExistsException;
import com.seydi.pharmacie.pharmacieapi.Exception.StockNotFoundException;
import com.seydi.pharmacie.pharmacieapi.Model.Produit;
import com.seydi.pharmacie.pharmacieapi.Model.Stock;
import com.seydi.pharmacie.pharmacieapi.Repository.ProduitRepository;
import com.seydi.pharmacie.pharmacieapi.Repository.StockRepository;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.StockResponse;
import com.seydi.pharmacie.pharmacieapi.mapper.StockMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final ProduitRepository produitRepository;
    private final StockMapper stockMapper;

    // Vérifier que le produit existe
    private Produit trouverProduitOuLeverException(Long id) {
        return produitRepository.findById(id) //chercher le produit
                .orElseThrow(() -> new ProduitNotFoundException("Produit introuvable")); //sinon lance une exception
    }

    private Stock trouverStockOuLeverException(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stock introuvable"));
    }


    public StockService(StockRepository stockRepository, ProduitRepository produitRepository, StockMapper stockMapper) {
        this.stockRepository = stockRepository;
        this.produitRepository = produitRepository;
        this.stockMapper = stockMapper;
    }

    // Ajouter un stock
    public StockResponse ajouterStock(CreateStockRequest request) {

        //Vérifier que le produit éxiste
        Produit produitExistant = trouverProduitOuLeverException(request.getProduitId());

        // Vérifier que ce produit ne possède pas déjà un stock
        if (stockRepository.existsByProduitId(request.getProduitId())) {
            throw new StockAlreadyExistsException("Ce produit possède déjà un stock.");
        }

        //Transformer le DTO en entité Stock
        Stock stock = stockMapper.toEntity(request);

        //Associer le stock au produit
        stock.setProduit(produitExistant);

        //Associer le produit au stock
        produitExistant.setStock(stock);

        // Sauvegarder le stock dans la base de données
        Stock stockSauvegarde = stockRepository.save(stock);

        // Retourner la réponse au frontend
        return stockMapper.toResponse(stockSauvegarde);
    }

    //Lister les produits avec leurs stock
    public List<StockResponse> listerStocks() {
        return stockRepository.findAll()
                .stream()
                .map(stock -> stockMapper.toResponse(stock))//Transforme chaque stock en StockResponse
                .toList(); //tout remetre dans une liste
    }

    //chercher un stock par son id
    public StockResponse chercherStockParId(Long id) {

        // S'il existe Transforme le Stock en StockResponse
        return stockMapper.toResponse(trouverStockOuLeverException(id));
    }

    // Modifier un stock
    public StockResponse modifierStock(Long id, UpdateStockRequest request) {

        // Rechercher le stock
        Stock stockExistant = trouverStockOuLeverException(id);

        // Mettre à jour la quantité
        stockMapper.updateEntity(stockExistant, request);

        // Sauvegarder les modifications
        Stock stockMisAJour = stockRepository.save(stockExistant);

        //Retrourner DTO au front
        return stockMapper.toResponse(stockMisAJour);

    }

    public void supprimerStock(Long id) {

        // Rechercher le stock à supprimer
        Stock stockExistant = trouverStockOuLeverException(id);

        // Récupérer le produit associé au stock
        Produit produit = stockExistant.getProduit();

        // Rompre la relation entre le produit et le stock
        stockExistant.setProduit(null);
        produit.setStock(null);

        // Supprimer définitivement le stock
        stockRepository.delete(stockExistant);
    }
}
