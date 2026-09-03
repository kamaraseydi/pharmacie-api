package com.seydi.pharmacie.pharmacieapi.controller;

import com.seydi.pharmacie.pharmacieapi.service.StockService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.StockResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;


    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    //Récupérer les produits avec leurs stock
    @Operation(
            summary = "Lister les stocks",
            description = "Retourne la liste des stocks."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des stocks récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs")
    })
    @GetMapping
    public List<StockResponse> listerStocks(){
        return stockService.listerStocks();
    }

    //Ajout de stock
    @Operation(
            summary = "Créer un stock",
            description = "Crée un stock et l'associe à un produit existant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Stock créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Produit introuvable"),
            @ApiResponse(responseCode = "409", description = "Le produit possède déjà un stock")
    })
    @PostMapping
    public ResponseEntity<StockResponse> ajouterStock(@Valid @RequestBody CreateStockRequest request){
        StockResponse stockResponse = stockService.ajouterStock(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockResponse);
    }

    //Chercher stock
    @Operation(
            summary = "Rechercher un stock",
            description = "Récupère un stock à partir de son identifiant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock trouvé"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Stock introuvable")
    })
    @GetMapping("/{id}")
    public StockResponse chercherStockParId(@Parameter(description = "Identifiant du stock", example = "1")
                                                @PathVariable Long id){
        return stockService.chercherStockParId(id);
    }

    //Modifier stock
    @Operation(
            summary = "Modifier un stock",
            description = "Modifie la quantité d'un stock existant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Stock introuvable")
    })
    @PutMapping("/{id}")
    public StockResponse modifierStock(@Parameter(description = "Identifiant du stock", example = "1")
                                           @PathVariable Long id, @Valid @RequestBody UpdateStockRequest request){
        return stockService.modifierStock(id,request);
    }

    //supprimer stock
    @Operation(
            summary = "Supprimer un stock",
            description = "Supprime un stock existant et rompt son association avec le produit."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Stock supprimé avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Stock introuvable")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerStock(@Parameter(description = "Identifiant du stock", example = "1")
                                                   @PathVariable Long id){

        stockService.supprimerStock(id);

        return ResponseEntity.noContent().build();
    }
}
