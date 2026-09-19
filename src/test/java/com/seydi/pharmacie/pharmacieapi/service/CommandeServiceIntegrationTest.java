package com.seydi.pharmacie.pharmacieapi.service;

import com.seydi.pharmacie.pharmacieapi.dto.request.CreateCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateLigneCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.model.Client;
import com.seydi.pharmacie.pharmacieapi.model.Fournisseur;
import com.seydi.pharmacie.pharmacieapi.model.Produit;
import com.seydi.pharmacie.pharmacieapi.model.Role;
import com.seydi.pharmacie.pharmacieapi.model.Stock;
import com.seydi.pharmacie.pharmacieapi.model.StatutCommande;
import com.seydi.pharmacie.pharmacieapi.repository.ClientRepository;
import com.seydi.pharmacie.pharmacieapi.repository.CommandeRepository;
import com.seydi.pharmacie.pharmacieapi.repository.FournisseurRepository;
import com.seydi.pharmacie.pharmacieapi.repository.ProduitRepository;
import com.seydi.pharmacie.pharmacieapi.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CommandeServiceIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.4");

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Test
    void ajouterCommande_commandeValide_persisteCommandeEtDiminueStock() {

        // 1. Créer le fournisseur
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setNom("Fournisseur Test");
        fournisseur.setAdresse("Dakar");
        fournisseur.setTelephone("771234569");
        fournisseur.setEmail("fournisseur.integration@test.com");

        fournisseur = fournisseurRepository.save(fournisseur);

        // 2. Créer le produit
        Produit produit = new Produit();
        produit.setNom("Paracetamol");
        produit.setDescription("Paracetamol 500mg");
        produit.setPrix(new BigDecimal("1500"));
        produit.setFournisseur(fournisseur);

        produit = produitRepository.save(produit);

        // 3. Créer le stock
        Stock stock = new Stock();
        stock.setProduit(produit);
        stock.setQuantite(10);

        stock = stockRepository.save(stock);

        // S'assurer que le produit référence bien son stock
        produit.setStock(stock);
        produit = produitRepository.save(produit);

        // 4. Créer le client
        Client client = new Client();
        client.setNom("Seydi");
        client.setEmail("client.integration@test.com");
        client.setMotDePasse("password123");
        client.setTelephone("771234568");
        client.setAdresse("Dakar");
        client.setRole(Role.CLIENT);

        client = clientRepository.save(client);

        // 5. Simuler le client authentifié
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName())
                .thenReturn(client.getId().toString());

        // 6. Préparer la requête
        CreateLigneCommandeRequest ligne =
                new CreateLigneCommandeRequest();

        ligne.setProduitId(produit.getId());
        ligne.setQuantite(2);

        CreateCommandeRequest request =
                new CreateCommandeRequest();

        request.setLignes(List.of(ligne));

        // 7. Exécuter la vraie méthode métier
        var response =
                commandeService.ajouterCommande(request, authentication);

        // 8. Vérifier que la réponse existe
        assertNotNull(response);

        // 9. Vérifier la commande réellement persistée
        var commandes =
                commandeRepository.findByClientId(client.getId());

        assertEquals(1, commandes.size());

        var commande = commandes.get(0);

        assertEquals(client.getId(), commande.getClient().getId());
        assertEquals(StatutCommande.EN_ATTENTE, commande.getStatut());
        assertEquals(
                new BigDecimal("3000"),
                commande.getTotalPrix()
        );

        assertNotNull(commande.getDateCommande());

        // 10. Vérifier la ligne de commande
        assertEquals(1, commande.getLigneCommandes().size());

        var ligneSauvegardee =
                commande.getLigneCommandes().get(0);

        assertEquals(produit.getId(), ligneSauvegardee.getProduit().getId());
        assertEquals(2, ligneSauvegardee.getQuantite());
        assertEquals(
                new BigDecimal("1500"),
                ligneSauvegardee.getPrixUnitaire()
        );

        // 11. Vérifier le stock
        Stock stockApresCommande =
                stockRepository.findByProduitId(produit.getId())
                        .orElseThrow();

        assertEquals(8, stockApresCommande.getQuantite());
    }
}