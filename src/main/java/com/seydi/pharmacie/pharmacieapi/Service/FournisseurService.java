package com.seydi.pharmacie.pharmacieapi.Service;

import com.seydi.pharmacie.pharmacieapi.Exception.FournisseurAlreadyExistsException;
import com.seydi.pharmacie.pharmacieapi.Exception.FournisseurHasProductsException;
import com.seydi.pharmacie.pharmacieapi.Exception.FournisseurNotFoundException;
import com.seydi.pharmacie.pharmacieapi.Model.Fournisseur;
import com.seydi.pharmacie.pharmacieapi.Repository.FournisseurRepository;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.FournisseurResponse;
import com.seydi.pharmacie.pharmacieapi.mapper.FournisseurMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final FournisseurMapper fournisseurMapper;

    private Fournisseur trouverFournisseurOuLeverException(Long id){
        return fournisseurRepository.findById(id)
                .orElseThrow(() -> new FournisseurNotFoundException("Fournisseur introuvable"));
    }

    public FournisseurService(FournisseurRepository fournisseurRepository,FournisseurMapper fournisseurMapper) {
        this.fournisseurRepository = fournisseurRepository;
        this.fournisseurMapper = fournisseurMapper;
    }

    //Ajout fournisseur
    public FournisseurResponse ajouterFournisseur(CreateFournisseurRequest request){

        if(fournisseurRepository.existsByNom(request.getNom())){
            throw new FournisseurAlreadyExistsException("Ce Fournisseur éxiste déja");
        }

        //Transforme le DTO en entité fournisseur
        Fournisseur fournisseur = fournisseurMapper.toEntity(request);

        //ajouter le fournisseur à la Base
        Fournisseur fournisseurSauvegarde = fournisseurRepository.save(fournisseur);

        // Retourner un DTO au frontend.
        return fournisseurMapper.toResponse(fournisseurSauvegarde);
    }

    //Lister tous les fournisseurs
    public List<FournisseurResponse> listerFournisseurs(){
        return fournisseurRepository.findAll()
                .stream()
                .map(fournisseur -> fournisseurMapper.toResponse(fournisseur))
                .toList();
    }

    //chercher un fournisseur par son Id
    public FournisseurResponse chercherFournisseurParId(Long id){
        return fournisseurMapper.toResponse(trouverFournisseurOuLeverException(id));
    }

    //modifer un fournisseur
    public FournisseurResponse modifierFournisseur(Long id, UpdateFournisseurRequest request){

        //chercher si le fournisseur existe dans la base
        Fournisseur fournisseurExistant = trouverFournisseurOuLeverException(id);

        //verifier si le nom a changer et revérifier s'il ne correspond
        //pas un au autre déja sauvegarder
        if(!fournisseurExistant.getNom().equalsIgnoreCase(request.getNom())){
            if(fournisseurRepository.existsByNom(request.getNom())){
                throw new FournisseurAlreadyExistsException("Ce Fournisseur éxiste déja");
            }
        }

        //mettre a jour l'objet fournisseurExistant
        fournisseurMapper.updateEntity(fournisseurExistant,request);

        //Sauvegarder les modifications dans la base.
        Fournisseur fournisseurSauvegarde = fournisseurRepository.save(fournisseurExistant);

        //Retoruner un DTO au frontEnd
        return fournisseurMapper.toResponse(fournisseurSauvegarde);
    }

    //Supprimer un fournisseur
    public void supprimerFournisseur(Long id){

        //Chercher si le fournisseur fournit existe
        Fournisseur fournisseurExistant = trouverFournisseurOuLeverException(id);

        // Vérifier que le fournisseur ne possède aucun produit
        if(!fournisseurExistant.getProduits().isEmpty()){
            throw new FournisseurHasProductsException("Impossible de supprimer ce fournisseur : " +
                    "des produits lui sont encore associés");
        }

        fournisseurRepository.delete(fournisseurExistant);
    }

}
