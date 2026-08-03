package com.seydi.pharmacie.pharmacieapi.Service;

import com.seydi.pharmacie.pharmacieapi.Exception.ProduitAlreadyExistsException;
import com.seydi.pharmacie.pharmacieapi.Exception.ProduitNotFoundException;
import com.seydi.pharmacie.pharmacieapi.Model.Produit;
import com.seydi.pharmacie.pharmacieapi.Repository.ProduitRepository;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ProduitResponse;
import com.seydi.pharmacie.pharmacieapi.mapper.ProduitMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;

    private Produit trouverProduitOuLeverException(Long id){
        return produitRepository.findById(id) //chercher le produit
                .orElseThrow(() -> new ProduitNotFoundException("Produit introuvable")); //sinon lance une exception
    }

    public ProduitService(ProduitRepository produitRepository, ProduitMapper produitMapper) {
        this.produitRepository = produitRepository;
        this.produitMapper = produitMapper;
    }

    //Lister tous les produits
    public List<ProduitResponse> listerProduits(){
        return produitRepository.findAll() //Récupère tous les produits.
                .stream() //Fais-les passer dans un flux.
                .map(produit -> produitMapper.toResponse(produit))  //Transforme chaque client en ClientResponse
                .toList(); //Remets le résultat dans une liste.
    }

    //Ajout produit
    public ProduitResponse ajouterProduit(CreateProduitRequest request){

        Produit produit = produitMapper.toEntity(request);

        //Vérifier si le nom du produit n'est pas déja utilisé
        if(produitRepository.existsByNom(produit.getNom())){
            throw new ProduitAlreadyExistsException("Produit déja existant");
        }

        //Ajout du produit
        Produit produitSauvegarde = produitRepository.save(produit);

        //Retourner un DTO au frontend
        return produitMapper.toResponse(produitSauvegarde);
    }

    //Chercher Produit par id
    public ProduitResponse chercherProduitParId(Long id){

        // S'il existe Transforme le Produit en ClientResponse
        return produitMapper.toResponse(trouverProduitOuLeverException(id));
    }

    //Modifier Produit
    public ProduitResponse modifierProduit(Long id, UpdateProduitRequest request){

        // 1. Rechercher le produit dans la base.
        // Si aucun produit ne possède cet id,
        // un ProduitNotFoundException est levée.

        Produit produitExistant = trouverProduitOuLeverException(id);

        // 2. Vérifier que le nouvel nom, on la changeait et s'il n'est pas déjà utilisé
        // par un autre produit.

        if(!produitExistant.getNom().equalsIgnoreCase(request.getNom())){
            if(produitRepository.existsByNom(request.getNom())){
                throw new ProduitAlreadyExistsException("Ce produit éxiste déja");
            }
        }

        // 3. Mettre à jour l'objet Produit existant
        // avec les nouvelles informations reçues.
        // Aucun nouvel objet Produit n'est créé.

        produitMapper.updateEntity(produitExistant,request);

        // 4. Sauvegarder les modifications dans la base.
        Produit produitSauvegarde = produitRepository.save(produitExistant);

        // 5. Retourner un DTO au frontend.
        return produitMapper.toResponse(produitSauvegarde);
    }

    //Supprimer un produit
    public void supprimerProduit(Long id){

        //Chercher le client ici, je n'ai pas besoin de l'objet client donc pas besoin de le stocker
        trouverProduitOuLeverException(id);

        //Supprimer le produit si trouvé
        produitRepository.deleteById(id);
    }
}
