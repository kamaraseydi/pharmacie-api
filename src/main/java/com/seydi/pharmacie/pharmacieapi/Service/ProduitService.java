package com.seydi.pharmacie.pharmacieapi.Service;

import com.seydi.pharmacie.pharmacieapi.Exception.FournisseurNotFoundException;
import com.seydi.pharmacie.pharmacieapi.Exception.ProduitAlreadyExistsException;
import com.seydi.pharmacie.pharmacieapi.Exception.ProduitHasStockException;
import com.seydi.pharmacie.pharmacieapi.Exception.ProduitNotFoundException;
import com.seydi.pharmacie.pharmacieapi.Model.Fournisseur;
import com.seydi.pharmacie.pharmacieapi.Model.Produit;
import com.seydi.pharmacie.pharmacieapi.Repository.FournisseurRepository;
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
    private final FournisseurRepository fournisseurRepository;

    private Produit trouverProduitOuLeverException(Long id){
        return produitRepository.findById(id) //chercher le produit
                .orElseThrow(() -> new ProduitNotFoundException("Produit introuvable")); //sinon lance une exception
    }

    private Fournisseur trouverFournisseurOuLeverException(Long id){
        return fournisseurRepository.findById(id)
                .orElseThrow(() -> new FournisseurNotFoundException("Fournisseur introuvable"));
    }

    public ProduitService(ProduitRepository produitRepository, ProduitMapper produitMapper, FournisseurRepository fournisseurRepository) {
        this.produitRepository = produitRepository;
        this.produitMapper = produitMapper;
        this.fournisseurRepository = fournisseurRepository;
    }

    //Lister tous les produits
    public List<ProduitResponse> listerProduits(){
        return produitRepository.findAll() //Récupère tous les produits.
                .stream() //Fais-les passer dans un flux.
                .map(produit -> produitMapper.toResponse(produit)) //Transforme chaque client en ClientResponse
                .toList(); //Remets le résultat dans une liste.
    }

    //Ajout produit
    public ProduitResponse ajouterProduit(CreateProduitRequest request){

        //Vérifier si le nom du produit n'est pas déja utilisé
        if(produitRepository.existsByNom(request.getNom())){
            throw new ProduitAlreadyExistsException("Produit déja existant");
        }

        //verifier si le fournisseur existe
        Fournisseur fournisseurExistant = trouverFournisseurOuLeverException(request.getFournisseurId());

        //Transformer le DTO en entité Produit
        Produit produit = produitMapper.toEntity(request);

        //Asoocier le produit au fournisseur
        produit.setFournisseur(fournisseurExistant);

        //Asoocier le fournisseur au produit
        fournisseurExistant.getProduits().add(produit);

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

        // Vérifier que fournisseur existe
        Fournisseur fournisseurExistant = trouverFournisseurOuLeverException(request.getFournisseurId());

        // 2. Vérifier que le nouvel nom, on la changeait et s'il n'est pas déjà utilisé
        // par un autre produit.

        if(!produitExistant.getNom().equalsIgnoreCase(request.getNom())){
            if(produitRepository.existsByNom(request.getNom())){
                throw new ProduitAlreadyExistsException("Ce produit éxiste déja");
            }
        }

        //Vérifier si l'ancien fournisseur est different du nouveau
        //si c'est le cas supprimer l'ancienne liaison(mettre à jour la relation)

        Fournisseur ancienFournisseur = produitExistant.getFournisseur();

        if(!ancienFournisseur.getId().equals(fournisseurExistant.getId())){

            // Retirer le produit de son ancien fournisseur
            ancienFournisseur.getProduits().remove(produitExistant);

            // Associer le produit au nouveau fournisseur
            produitExistant.setFournisseur(fournisseurExistant);

            // Ajouter le produit au nouveau fournisseur
            fournisseurExistant.getProduits().add(produitExistant);
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

        //Chercher le produit ici
       Produit produitExistant =  trouverProduitOuLeverException(id);

       //vérifier si le stock du produit est vide avant de supprimer
        if(produitExistant.getStock() != null){
            throw new ProduitHasStockException("\"Impossible de supprimer ce produit : " +
                    "un stock lui est encore associé.");
        }

       //Récupérer le fournisseur associé au produit
        Fournisseur fournisseurExistant = produitExistant.getFournisseur();

        // Retirer le produit de la liste de son fournisseur
        //Rompre la relation entre le produit et le fournisseur
        fournisseurExistant.getProduits().remove(produitExistant);
        produitExistant.setFournisseur(null);

        //Supprimer le produit si trouvé
        produitRepository.delete(produitExistant);
    }
}
