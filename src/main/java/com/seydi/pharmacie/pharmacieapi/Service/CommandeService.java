package com.seydi.pharmacie.pharmacieapi.Service;

import com.seydi.pharmacie.pharmacieapi.Exception.*;
import com.seydi.pharmacie.pharmacieapi.Model.*;
import com.seydi.pharmacie.pharmacieapi.Repository.ClientRepository;
import com.seydi.pharmacie.pharmacieapi.Repository.CommandeRepository;
import com.seydi.pharmacie.pharmacieapi.Repository.ProduitRepository;
import com.seydi.pharmacie.pharmacieapi.Repository.StockRepository;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateLigneCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStatutCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.CommandeResponse;
import com.seydi.pharmacie.pharmacieapi.mapper.CommandeMapper;
import com.seydi.pharmacie.pharmacieapi.mapper.LigneCommandeMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final StockRepository stockRepository;

    private final CommandeMapper commandeMapper;
    private final LigneCommandeMapper ligneCommandeMapper;

    public CommandeService(CommandeRepository commandeRepository, ClientRepository clientRepository, ProduitRepository produitRepository, StockRepository stockRepository, CommandeMapper commandeMapper, LigneCommandeMapper ligneCommandeMapper){
        this.commandeRepository = commandeRepository;
        this.clientRepository = clientRepository;
        this.produitRepository = produitRepository;
        this.stockRepository = stockRepository;
        this.commandeMapper = commandeMapper;
        this.ligneCommandeMapper = ligneCommandeMapper;
    };


    private Client trouverClientOuLeverException(Long id){
        return clientRepository.findById(id) //Cherche le client.
                .orElseThrow(()-> new ClientNotFoundException("Client introuvable")); //sinon lance une exception
    }

    private Produit trouverProduitOuLeverException(Long id){
        return produitRepository.findById(id) //chercher le produit
                .orElseThrow(() -> new ProduitNotFoundException("Produit introuvable")); //sinon lance une exception
    }

    private Commande trouverCommandeOuLeverException(Long id){
        return commandeRepository.findById(id)
                .orElseThrow(() -> new CommandeNotFoundException("Commande introuvable"));
    }

    private Client trouverClientConnecterOuLeverException(Authentication authentication) {

        //je fait ça parce que l'ID est transformé en String lors de la construction du UserDetails
        Long clientId = Long.valueOf(authentication.getName());

        return trouverClientOuLeverException(clientId);
    }

    //Ajouter Commande
    @Transactional
    public CommandeResponse ajouterCommande(CreateCommandeRequest request,Authentication authentication){

        //une structure pour mémoriser les stocks verrouillés
        Map<Long, Stock> stocksVerrouilles = new HashMap<>();

        //Vérifier que le client connecté éxiste
        Client clientExistant = trouverClientConnecterOuLeverException(authentication);

        //Transformer le DTO en entité Commande
        Commande commande = commandeMapper.toEntity(request);

        //Associer la commande au client
        commande.setClient(clientExistant);

        //Associer le client au commande
        clientExistant.getCommandes().add(commande);

        //infos automatique de la commande
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatut(StatutCommande.EN_ATTENTE);

        //Pour éviter d'ajouter 2 fois le mm produit
        Set<Long> produitsDejaAjoutes = new HashSet<>();

        //initialiser le prix total à 0
        BigDecimal totalPrix = BigDecimal.ZERO;

        //Parcourir chaque ligne de commande
        for(CreateLigneCommandeRequest ligneCommandeRequest : request.getLignes()){

            Long produitId = ligneCommandeRequest.getProduitId();

            //Vérifier si le produit n'a pas déja été ajouté
            //true → l'élément n'était pas encore présent
            //false → l'élément était déjà présent
            if (!produitsDejaAjoutes.add(produitId)) {
                throw new ProduitDejaDansCommandeException(
                        "Un même produit ne peut pas apparaître plusieurs fois dans une commande"
                );
            }

            //Récupérer le produit et vérifier s'il existe
            Produit produitExistant = trouverProduitOuLeverException(ligneCommandeRequest.getProduitId());

            //Récupérer le stock avec verrou pessimiste
            Optional<Stock> stockVerrous = stockRepository.findByProduitId(ligneCommandeRequest.getProduitId());

            //vérifier le stock
            if(stockVerrous.isEmpty()){
                throw new StockInsuffisantException("Ce produit ne posséde pas de stock");
            }

            //récupère le stock verrouillé
            Stock stock = stockVerrous.get();

            //Enregistrer le stock dans la structure
            stocksVerrouilles.put(produitId,stock);

            // Vérifier le stock verrouillé
            if(stock.getQuantite() < ligneCommandeRequest.getQuantite()){
                throw new StockInsuffisantException("Stock insuffisant");
            }

            //Transformer le DTO en entité Ligne Commande
            LigneCommande ligneCommande = ligneCommandeMapper.toEntity(ligneCommandeRequest);

            //Associer la ligne commande à la commande
            ligneCommande.setCommande(commande);

            //Associer la commande a la ligne commande
            commande.getLigneCommandes().add(ligneCommande);

            //Associer la ligne commande au produit
            ligneCommande.setProduit(produitExistant);

            //Associer le produit à la ligne commande
            produitExistant.getLigneCommandes().add(ligneCommande);

            //Récupérer son prix unitaire
            ligneCommande.setPrixUnitaire(produitExistant.getPrix());

            //Calculer le prix total ici on utilise multiply car on multiplie des BigDecimal
            BigDecimal prixLigne = produitExistant.getPrix().multiply(BigDecimal.valueOf(ligneCommande.getQuantite()));

            // Ajouter le prix de la ligne Commande au total
            totalPrix = totalPrix.add(prixLigne);

        }

        // Enregistrer le total dans la commande
        commande.setTotalPrix(totalPrix);

        //Refaire la boucle pour modifier les stocks des produits
        for(CreateLigneCommandeRequest ligneCommandeRequest : request.getLignes()){

            //récupérer le stock que nous avons déjà verrouillé
            Stock stock = stocksVerrouilles.get(ligneCommandeRequest.getProduitId());

            //récupérer la quantite dans la ligne
            Integer quantiteCommandee = ligneCommandeRequest.getQuantite();

            //Calculer son nouveau quantité
            stock.setQuantite(stock.getQuantite() - quantiteCommandee);

            //sauvegarder la modification(on peut ne pas le faire car avec @Transactionnal il le fait automatiquement)
        }

        //Sauvegarder la commande
        Commande commandeSauvegarde = commandeRepository.save(commande);

        //Retourner un DTO au frontend
        return commandeMapper.toResponse(commandeSauvegarde);
    }

    //lister les commandes
    public List<CommandeResponse> listerCommandes(Authentication authentication){

        if(estAdmin(authentication)){
            //Retourner toutes les commandes
            return commandeRepository.findAll()
                    .stream()
                    .map(commande -> commandeMapper.toResponse(commande))
                    .toList();
        }else {

            // récupérer uniquement les commandes du client connecté
            return commandeRepository.findByClientId(Long.valueOf(authentication.getName()))
                    .stream()
                    .map(commandeMapper::toResponse)
                    .toList();
        }

    }

    //Vérifier si l'utilisateur connecté est un admin
    private boolean estAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority()
                        .equals("ROLE_ADMIN"));
    }

    //Vérifier qui aura accés a la commande
    private void verifierAccesCommande(Commande commande, Authentication authentication) throws AccessDeniedException {
        if(estAdmin(authentication)){
            return;
        }

        if(!commande.getClient().getId().equals(Long.valueOf(authentication.getName()))){
            throw new AccessDeniedException("Accés refusé");
        }

    }

    //chercher une commande par son id
    public CommandeResponse chercherCommandeParId(Long id,Authentication authentication) throws AccessDeniedException {

            Commande  commande = trouverCommandeOuLeverException(id);

            verifierAccesCommande(commande,authentication);

            return commandeMapper.toResponse(commande);
    }

    // Vérifier si la transition de statut est autorisée
    private void verifierTransitionStatut(StatutCommande ancienStatut, StatutCommande nouveauStatut) {

        // Une commande livrée ou annulée ne peut plus changer de statut
        if (ancienStatut == StatutCommande.LIVREE || ancienStatut == StatutCommande.ANNULEE) {

            throw new CommandeTransitionException("Impossible de modifier une commande " + ancienStatut);
        }

        // Une commande en attente peut être confirmée ou annulée
        if (ancienStatut == StatutCommande.EN_ATTENTE && nouveauStatut != StatutCommande.CONFIRMEE
                && nouveauStatut != StatutCommande.ANNULEE) {

            throw new CommandeTransitionException("Transition de statut impossible : "
                    + ancienStatut + " → " + nouveauStatut
            );
        }

        // Une commande confirmée peut être mise en préparation ou annulée
        if (ancienStatut == StatutCommande.CONFIRMEE && nouveauStatut != StatutCommande.EN_PREPARATION
                && nouveauStatut != StatutCommande.ANNULEE) {

            throw new CommandeTransitionException("Transition de statut impossible : "
                            + ancienStatut + " → " + nouveauStatut
            );
        }

        // Une commande en préparation peut uniquement être prête
        if (ancienStatut == StatutCommande.EN_PREPARATION && nouveauStatut != StatutCommande.PRETE) {

            throw new CommandeTransitionException("Transition de statut impossible : "
                            + ancienStatut + " → " + nouveauStatut
            );
        }

        // Une commande prête peut uniquement être livrée
        if (ancienStatut == StatutCommande.PRETE && nouveauStatut != StatutCommande.LIVREE) {

            throw new CommandeTransitionException("Transition de statut impossible : "
                            + ancienStatut + " → " + nouveauStatut
            );
        }
    }

    //modifier le statut d'une commande
    @Transactional //Ce n'est pas aussi critique que pour ajouterCommande() où plusieurs stocks + la commande sont modifiés,
    // mais c'est une bonne pratique pour une opération métier qui doit être atomique.
    public CommandeResponse modifierStatutCommande(Long id, UpdateStatutCommandeRequest request){
        //Chercher la commande
        Commande commandeExistant = trouverCommandeOuLeverException(id);

        //Veérifier si le changement de statut est autorisé plus tard
        verifierTransitionStatut(commandeExistant.getStatut(), request.getStatut());

        //Mettre a jour l'objet commande existant
        commandeMapper.updateEntity(commandeExistant,request);

        //sauvegarder la commande
        Commande commandeSauvegarde = commandeRepository.save(commandeExistant);

        //retourner un DTO au front
        return commandeMapper.toResponse(commandeSauvegarde);
    }

    //Annuler une commande
    @Transactional
    public CommandeResponse annulerCommande(Long id,Authentication authentication) {
        //chercher la commande
        Commande commandeExistant = trouverCommandeOuLeverException(id);

        //Vérifier l'accés
        verifierAccesCommande(commandeExistant,authentication);

        //Vérifier s'il peut etre annuler
        verifierTransitionStatut(commandeExistant.getStatut(),StatutCommande.ANNULEE);

        //parcourir les lignes commandes
        for(LigneCommande ligneCommande : commandeExistant.getLigneCommandes()){
            //Récuperer le produit
            Produit produitExistant = ligneCommande.getProduit();

            //Récupérer la quantite dans le  stock du produit
            Integer stockProduit = produitExistant.getStock().getQuantite();

            //Récupérer la quantité commandé
            Integer qteCommande = ligneCommande.getQuantite();

            //Ajouter la quantite commandé au stock
            produitExistant.getStock().setQuantite(stockProduit + qteCommande);

            //Et c'est justement un bon exemple de l'intérêt de @Transactional
            //Le Stock est une entité gérée par Hibernate dans la transaction. Lorsque tu fais :
            //Hibernate détectera la modification et la synchronisera avec la base au commit.
            //Donc contrairement à ta première méthode où on avait explicitement fait :
            //Ici ce n'est pas nécessaire
        }

        //changer le statut -> Annule
        commandeExistant.setStatut(StatutCommande.ANNULEE);

        //sauvegarder
        Commande commandeSauvegarde = commandeRepository.save(commandeExistant);

        //retourner un DTO au front
        return commandeMapper.toResponse(commandeSauvegarde);
    }
}
