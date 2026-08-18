package com.seydi.pharmacie.pharmacieapi.Controller;

import com.seydi.pharmacie.pharmacieapi.Service.FournisseurService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.FournisseurResponse;
import jakarta.validation.Valid;
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
    @PostMapping()
    public FournisseurResponse ajouterFournisseur(@Valid @RequestBody CreateFournisseurRequest request){
        return fournisseurService.ajouterFournisseur(request);
    }

    //lister les fournisseurs
    @GetMapping()
    public List<FournisseurResponse> listerFournisseurs(){
        return fournisseurService.listerFournisseurs();
    }

    //chercher un fournisseur par id
    @GetMapping("/{id}")
    public FournisseurResponse chercherFournisseurParId(@PathVariable Long id){
        return fournisseurService.chercherFournisseurParId(id);
    }

    //modifier un fournisseur
    @PutMapping("/{id}")
    public FournisseurResponse modifierFournisseur(@PathVariable Long id, @Valid @RequestBody UpdateFournisseurRequest request){
        return fournisseurService.modifierFournisseur(id,request);
    }

    //supprimer fournisseur
    @DeleteMapping("/{id}")
    public void supprimerFournisseur(@PathVariable Long id){
        fournisseurService.supprimerFournisseur(id);
    }
}
