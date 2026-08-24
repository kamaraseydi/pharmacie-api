package com.seydi.pharmacie.pharmacieapi.Controller;

import com.seydi.pharmacie.pharmacieapi.Service.CommandeService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStatutCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.CommandeResponse;
import jakarta.validation.Valid;
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
    public CommandeResponse ajouterCommande(@Valid @RequestBody CreateCommandeRequest request){
        return commandeService.ajouterCommande(request);
    }

    //Lister les commandes
    @GetMapping
    public List<CommandeResponse> listerCommandes(){
        return commandeService.listerCommandes();
    }

    //Chercher commande par Id
    @GetMapping("/{id}")
    public CommandeResponse chercherCommandeParId(@PathVariable Long id){
        return commandeService.chercherCommandeParId(id);
    }


    //Modifier statut commande
    @PutMapping("/{id}/statut")
    public CommandeResponse modifierStatutCommande(@PathVariable Long id,@Valid @RequestBody UpdateStatutCommandeRequest request){
        return commandeService.modifierStatutCommande(id,request);
    }

    //Annuler une commande
    @PutMapping("/{id}/annuler")
    public CommandeResponse annulerCommande(@PathVariable Long id){
        return commandeService.annulerCommande(id);
    }
}
