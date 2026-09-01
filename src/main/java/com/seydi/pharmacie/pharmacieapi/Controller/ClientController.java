package com.seydi.pharmacie.pharmacieapi.Controller;

import com.seydi.pharmacie.pharmacieapi.Service.ClientService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ClientResponse;
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
    @GetMapping
    public List<ClientResponse> listerClients() {
        return clientService.listerClients();
    }

    //ici c'est pour ajouter un client
    @PostMapping
    public ResponseEntity<ClientResponse> ajouterClient(@Valid @RequestBody CreateClientRequest request) {
        ClientResponse clientResponse = clientService.ajouterClient(request);

        return ResponseEntity.status(HttpStatus.CREATED) //Ceci c'est pour que ca retourne 201 qui témoigne de sa création
                .body(clientResponse);
    }

    // Profil du client connecté
    @GetMapping("/me")
    public ClientResponse chercherMonProfil(Authentication authentication) {
        return clientService.chercherMonProfil(authentication);
    }

    //pour rechercher un client par son id
    @GetMapping("/{id}")
    public ClientResponse chercherClientParId(@PathVariable Long id, Authentication authentication) {
        return clientService.chercherClientParId(id,authentication);
    }

    //Pour modifier profil du client connecter
    @PutMapping("/me")
    public ClientResponse modifierMonProfil(Authentication authentication,@Valid @RequestBody UpdateClientRequest request) {
        return clientService.modifierMonProfil(authentication, request);
    }

    @PutMapping("/{id}")
    public ClientResponse modifierClient(@PathVariable Long id,Authentication authentication,@Valid @RequestBody UpdateClientRequest request){
        return clientService.modifierClient(id,authentication,request);
    }

    //Pour supprimer son profil
    @DeleteMapping("/me")
    public ResponseEntity<Void> supprimerMonProfil(Authentication authentication) {
        clientService.supprimerMonProfil(authentication);

        return ResponseEntity.noContent().build();//ici c'est pour 204 genre No Cotent
    }
    //Pour supprimer un client
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerClient(@PathVariable Long id,Authentication authentication) {
        clientService.supprimerClient(id,authentication);

        return ResponseEntity.noContent().build();//ici c'est pour 204 genre No Cotent
    }

}
