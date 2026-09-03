package com.seydi.pharmacie.pharmacieapi.controller;

import com.seydi.pharmacie.pharmacieapi.service.ProduitService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ProduitResponse;
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
@RequestMapping("/produits")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    //Récupérer les produits et les lister
    @Operation(
            summary = "Lister les produits",
            description = "Retourne la liste des produits disponibles."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des produits récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise")
    })
    @GetMapping
    public List<ProduitResponse> listerProduits(){
        return produitService.listerProduits();
    }

    //Ajout d'un produit
    @Operation(
            summary = "Créer un produit",
            description = "Crée un nouveau produit et l'associe à un fournisseur existant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produit créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable"),
            @ApiResponse(responseCode = "409", description = "Produit déjà existant")
    })
    @PostMapping
    public ResponseEntity<ProduitResponse> ajouterProduit(@Valid @RequestBody CreateProduitRequest request){
        ProduitResponse produitResponse = produitService.ajouterProduit(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(produitResponse);
    }

    //Chercher un produit par son id
    @Operation(
            summary = "Rechercher un produit",
            description = "Récupère un produit à partir de son identifiant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produit trouvé"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "404", description = "Produit introuvable")
    })
    @GetMapping("/{id}")
    public ProduitResponse chercherProduitParId(@Parameter(description = "Identifiant du produit", example = "1")
                                                    @PathVariable Long id){
        return produitService.chercherProduitParId(id);
    }

    //modifier un produit
    @Operation(
            summary = "Modifier un produit",
            description = "Modifie un produit existant et, si nécessaire, son fournisseur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produit modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Produit ou fournisseur introuvable"),
            @ApiResponse(responseCode = "409", description = "Nom du produit déjà utilisé")
    })
    @PutMapping("/{id}")
    public ProduitResponse modifierProduit(@Parameter(description = "Identifiant du produit", example = "1")
                                               @PathVariable Long id, @Valid @RequestBody UpdateProduitRequest request){
        return produitService.modifierProduit(id,request);
    }

    //Supprimer Produit
    @Operation(
            summary = "Supprimer un produit",
            description = "Supprime un produit uniquement lorsqu'aucun stock ne lui est encore associé."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Produit introuvable"),
            @ApiResponse(responseCode = "409", description = "Le produit possède encore un stock associé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerProduit(@Parameter(description = "Identifiant du produit", example = "1")
                                                     @PathVariable Long id){

        produitService.supprimerProduit(id);

        return ResponseEntity.noContent().build();
    }

}
