package com.seydi.pharmacie.pharmacieapi.controller;

import com.seydi.pharmacie.pharmacieapi.service.CommandeService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStatutCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.CommandeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commandes")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    //Ajouter une commande
    @PostMapping
    @Operation(
            summary = "Créer une commande",
            description = "Crée une nouvelle commande pour le client actuellement authentifié."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Commande créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de la commande invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "409", description = "Stock insuffisant ou produit dupliqué")
    })
    public ResponseEntity<CommandeResponse> ajouterCommande(@Valid @RequestBody CreateCommandeRequest request, Authentication authentication){
        CommandeResponse commandeResponse = commandeService.ajouterCommande(request,authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commandeResponse);
    }

    //Lister les commandes
    @Operation(
            summary = "Lister les commandes",
            description = "Retourne toutes les commandes pour un administrateur ou uniquement les commandes du client authentifié."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Commandes récupérées avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise")
    })
    @GetMapping
    public List<CommandeResponse> listerCommandes(Authentication authentication){
        return commandeService.listerCommandes(authentication);
    }

    //Chercher commande par Id
    @Operation(
            summary = "Rechercher une commande",
            description = "Récupère une commande à partir de son identifiant. Un client peut uniquement consulter ses propres commandes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Commande trouvée"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
            @ApiResponse(responseCode = "404", description = "Commande introuvable")
    })
    @GetMapping("/{id}")
    public CommandeResponse chercherCommandeParId(@Parameter(description = "Identifiant de la commande", example = "15")
                                                      @PathVariable Long id, Authentication authentication) throws AccessDeniedException {
        return commandeService.chercherCommandeParId(id,authentication);
    }

    //Modifier statut commande
    @Operation(
            summary = "Modifier le statut d'une commande",
            description = "Modifie le statut d'une commande. Cette opération est réservée aux administrateurs et respecte les transitions de statut autorisées."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut de la commande modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données du statut invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Commande introuvable"),
            @ApiResponse(responseCode = "409", description = "Transition de statut impossible")
    })
    @PutMapping("/{id}/statut")
    public CommandeResponse modifierStatutCommande( @Parameter(description = "Identifiant de la commande", example = "15")
                                                        @PathVariable Long id,@Valid @RequestBody UpdateStatutCommandeRequest request){
        return commandeService.modifierStatutCommande(id,request);
    }

    //Annuler une commande
    @Operation(
            summary = "Annuler une commande",
            description = "Annule une commande. Un client peut uniquement annuler sa propre commande, tandis qu'un administrateur peut annuler n'importe quelle commande."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Commande annulée avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
            @ApiResponse(responseCode = "404", description = "Commande introuvable"),
            @ApiResponse(responseCode = "409", description = "La commande ne peut pas être annulée")
    })
    @PutMapping("/{id}/annuler")
    public CommandeResponse annulerCommande(@Parameter(description = "Identifiant de la commande", example = "15")
                                                @PathVariable Long id, Authentication authentication){
        return commandeService.annulerCommande(id, authentication);
    }
}
