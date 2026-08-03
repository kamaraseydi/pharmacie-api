package com.seydi.pharmacie.pharmacieapi.Controller;

import com.seydi.pharmacie.pharmacieapi.Service.ProduitService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ProduitResponse;
import jakarta.validation.Valid;
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
    @GetMapping
    public List<ProduitResponse> listerProduits(){
        return produitService.listerProduits();
    }

    //Ajout d'un produit
    @PostMapping
    public ProduitResponse ajouterProduit(@Valid @RequestBody CreateProduitRequest request){
        return produitService.ajouterProduit(request);
    }

    //Chercher un produit par son id
    @GetMapping("/{id}")
    public ProduitResponse chercherProduitParId(@PathVariable Long id){
        return produitService.chercherProduitParId(id);
    }

    //modifier un produit
    @PutMapping("/{id}")
    public ProduitResponse modifierProduit(@PathVariable Long id, @Valid @RequestBody UpdateProduitRequest request){
        return produitService.modifierProduit(id,request);
    }

    //Supprimer Produit
    @DeleteMapping("/{id}")
    public void supprimerProduit(@PathVariable Long id){
        produitService.supprimerProduit(id);
    }

}
