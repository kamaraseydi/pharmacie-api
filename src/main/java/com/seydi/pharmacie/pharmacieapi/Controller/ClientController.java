package com.seydi.pharmacie.pharmacieapi.controller;

import com.seydi.pharmacie.pharmacieapi.service.ClientService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ClientResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/clients") //pour éviter de le répéter à chaque fois

public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    //ici c'est pour récupérer les clients et les lister
    @Operation(
            summary = "Lister les clients",
            description = "Retourne la liste de tous les clients. Cette opération est réservée aux administrateurs."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs")
    })
    @GetMapping
    public List<ClientResponse> listerClients() {
        return clientService.listerClients();
    }

    //ici c'est pour ajouter un client
    @Operation(
            summary = "Créer un client",
            description = "Crée un nouveau compte client."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    @SecurityRequirements //cette méthode ne nécéssite pas d'etre authentifier
    @PostMapping
    public ResponseEntity<ClientResponse> ajouterClient(@Valid @RequestBody CreateClientRequest request) {
        ClientResponse clientResponse = clientService.ajouterClient(request);

        return ResponseEntity.status(HttpStatus.CREATED) //Ceci c'est pour que ca retourne 201 qui témoigne de sa création
                .body(clientResponse);
    }

    // Profil du client connecté

    @Operation(//pour décrire ce que fait cette route
            summary = "Consulter mon profil",
            description = "Récupère le profil du client actuellement authentifié."
    )
    @ApiResponses({//pour montrer les ce qu'il peut retourner dans ces situations
            @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise")
    })
    @GetMapping("/me")
    public ClientResponse chercherMonProfil(Authentication authentication) {
        return clientService.chercherMonProfil(authentication);
    }

    //pour rechercher un client par son id
    @Operation(
            summary = "Rechercher un client",
            description = "Récupère les informations d'un client à partir de son identifiant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @GetMapping("/{id}") //@Parameter → quels paramètres sont attendus ?
    public ClientResponse chercherClientParId(@Parameter(description = "Identifiant du client", example = "15")
                                                  @PathVariable Long id, Authentication authentication) {
        return clientService.chercherClientParId(id,authentication);
    }

    //Pour modifier profil du client connecter
    @Operation(
            summary = "Modifier mon profil",
            description = "Modifie les informations du profil du client actuellement authentifié."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    @PutMapping("/me")
    // @SecurityRequirement(name = "bearerAuth")//pour dire a swagger cette partie nécéssite une authentification
    public ClientResponse modifierMonProfil(Authentication authentication,@Valid @RequestBody UpdateClientRequest request) {
        return clientService.modifierMonProfil(authentication, request);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Modifier un client",
            description = "Modifie les informations d'un client. Cette opération est réservée aux administrateurs."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Client introuvable"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ClientResponse modifierClient(@Parameter(description = "Identifiant du client", example = "15")
                                             @PathVariable Long id,Authentication authentication,@Valid @RequestBody UpdateClientRequest request){
        return clientService.modifierClient(id,authentication,request);
    }

    //Pour supprimer son profil
    @Operation(
            summary = "Supprimer mon profil",
            description = "Supprime le compte du client actuellement authentifié lorsqu'aucune commande ne lui est associée."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profil supprimé avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "409", description = "Le client possède des commandes associées")
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> supprimerMonProfil(Authentication authentication) {
        clientService.supprimerMonProfil(authentication);

        return ResponseEntity.noContent().build();//ici c'est pour 204 genre No Cotent
    }
    //Pour supprimer un client
    @Operation(
            summary = "Supprimer un client",
            description = "Supprime un client. Cette opération est réservée aux administrateurs et n'est possible que si aucune commande ne lui est associée."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Client supprimé avec succès"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux administrateurs"),
            @ApiResponse(responseCode = "404", description = "Client introuvable"),
            @ApiResponse(responseCode = "409", description = "Le client possède des commandes associées")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerClient(@Parameter(description = "Identifiant du client", example = "15")
                                                    @PathVariable Long id,Authentication authentication) {
        clientService.supprimerClient(id,authentication);

        return ResponseEntity.noContent().build();//ici c'est pour 204 genre No Cotent
    }

}
