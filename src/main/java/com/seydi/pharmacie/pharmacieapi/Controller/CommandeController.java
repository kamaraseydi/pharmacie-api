package com.seydi.pharmacie.pharmacieapi.Controller;

import com.seydi.pharmacie.pharmacieapi.Service.CommandeService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStatutCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.CommandeResponse;
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
    public ResponseEntity<CommandeResponse> ajouterCommande(@Valid @RequestBody CreateCommandeRequest request, Authentication authentication){
        CommandeResponse commandeResponse = commandeService.ajouterCommande(request,authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commandeResponse);
    }

    //Lister les commandes
    @GetMapping
    public List<CommandeResponse> listerCommandes(Authentication authentication){
        return commandeService.listerCommandes(authentication);
    }

    //Chercher commande par Id
    @GetMapping("/{id}")
    public CommandeResponse chercherCommandeParId(@PathVariable Long id, Authentication authentication) throws AccessDeniedException {
        return commandeService.chercherCommandeParId(id,authentication);
    }


    //Modifier statut commande
    @PutMapping("/{id}/statut")
    public CommandeResponse modifierStatutCommande(@PathVariable Long id,@Valid @RequestBody UpdateStatutCommandeRequest request){
        return commandeService.modifierStatutCommande(id,request);
    }

    //Annuler une commande
    @PutMapping("/{id}/annuler")
    public CommandeResponse annulerCommande(@PathVariable Long id, Authentication authentication){
        return commandeService.annulerCommande(id, authentication);
    }
}
