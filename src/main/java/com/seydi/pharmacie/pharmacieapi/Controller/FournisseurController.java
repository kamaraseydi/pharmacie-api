package com.seydi.pharmacie.pharmacieapi.controller;

import com.seydi.pharmacie.pharmacieapi.service.FournisseurService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.FournisseurResponse;
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
@RequestMapping("/fournisseurs")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    public FournisseurController(FournisseurService fournisseurService) {
        this.fournisseurService = fournisseurService;
    }

    //Ajouter fournisseur
    @Operation(
            summary = "Créer un fournisseur",
            description = "Crée un nouveau fournisseur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fournisseur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "409", description = "Fournisseur déjà existant")
    })
    @PostMapping()
    public ResponseEntity<FournisseurResponse> ajouterFournisseur(@Valid @RequestBody CreateFournisseurRequest request){
        FournisseurResponse fournisseurResponse = fournisseurService.ajouterFournisseur(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fournisseurResponse);
    }

    //lister les fournisseurs
    @GetMapping()
    @Operation(
            summary = "Lister les fournisseurs",
            description = "Retourne la liste des fournisseurs."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des fournisseurs récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs")
    })
    public List<FournisseurResponse> listerFournisseurs(){
        return fournisseurService.listerFournisseurs();
    }

    //chercher un fournisseur par id
    @Operation(
            summary = "Rechercher un fournisseur",
            description = "Récupère un fournisseur à partir de son identifiant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fournisseur trouvé"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable")
    })
    @GetMapping("/{id}")
    public FournisseurResponse chercherFournisseurParId(@Parameter(description = "Identifiant du fournisseur", example = "1")
                                                            @PathVariable Long id){
        return fournisseurService.chercherFournisseurParId(id);
    }

    //modifier un fournisseur
    @Operation(
            summary = "Modifier un fournisseur",
            description = "Modifie les informations d'un fournisseur existant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fournisseur modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable"),
            @ApiResponse(responseCode = "409", description = "Conflit lors de la modification du fournisseur")
    })
    @PutMapping("/{id}")
    public FournisseurResponse modifierFournisseur(@Parameter(description = "Identifiant du fournisseur", example = "1")
                                                       @PathVariable Long id, @Valid @RequestBody UpdateFournisseurRequest request){
        return fournisseurService.modifierFournisseur(id,request);
    }

    //supprimer fournisseur
    @Operation(
            summary = "Supprimer un fournisseur",
            description = "Supprime un fournisseur existant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Fournisseur supprimé avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable"),
            @ApiResponse(responseCode = "409", description = "Le fournisseur ne peut pas être supprimé dans son état actuel")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerFournisseur(@Parameter(description = "Identifiant du fournisseur", example = "1")
                                                         @PathVariable Long id){
        fournisseurService.supprimerFournisseur(id);

        return ResponseEntity.noContent().build();
    }
}
