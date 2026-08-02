package com.seydi.pharmacie.pharmacieapi.Controller;

import com.seydi.pharmacie.pharmacieapi.Service.ClientService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ClientResponse;
import jakarta.validation.Valid;
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
    public ClientResponse ajouterClient(@Valid @RequestBody CreateClientRequest request) {
        return clientService.ajouterClient(request);
    }

    //pour rechercher un client par son id
    @GetMapping("/{id}")
    public ClientResponse chercherClientParId(@PathVariable Long id) {
        return clientService.chercherClientParId(id);
    }

    //Pour modifier un client
    @PutMapping("/{id}")
    public ClientResponse modifierClient(@PathVariable Long id,@Valid @RequestBody UpdateClientRequest request) {
        return clientService.modifierClient(id, request);
    }

    //Pour supprimer un client
    @DeleteMapping("/{id}")
    public void supprimerClient(@PathVariable Long id) {
        clientService.supprimerClient(id);
    }

}
