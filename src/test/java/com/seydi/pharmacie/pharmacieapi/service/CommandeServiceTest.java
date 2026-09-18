package com.seydi.pharmacie.pharmacieapi.service;

import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStatutCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.CommandeResponse;
import com.seydi.pharmacie.pharmacieapi.exception.CommandeNotFoundException;
import com.seydi.pharmacie.pharmacieapi.exception.CommandeTransitionException;
import com.seydi.pharmacie.pharmacieapi.model.Client;
import com.seydi.pharmacie.pharmacieapi.mapper.LigneCommandeMapper;
import com.seydi.pharmacie.pharmacieapi.model.Commande;
import com.seydi.pharmacie.pharmacieapi.model.StatutCommande;
import com.seydi.pharmacie.pharmacieapi.repository.ClientRepository;
import com.seydi.pharmacie.pharmacieapi.repository.CommandeRepository;
import com.seydi.pharmacie.pharmacieapi.repository.ProduitRepository;
import com.seydi.pharmacie.pharmacieapi.repository.StockRepository;
import com.seydi.pharmacie.pharmacieapi.mapper.CommandeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.seydi.pharmacie.pharmacieapi.dto.request.CreateCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateLigneCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.exception.ClientNotFoundException;
import com.seydi.pharmacie.pharmacieapi.exception.ProduitDejaDansCommandeException;
import com.seydi.pharmacie.pharmacieapi.exception.ProduitNotFoundException;
import com.seydi.pharmacie.pharmacieapi.exception.StockInsuffisantException;
import com.seydi.pharmacie.pharmacieapi.model.LigneCommande;
import com.seydi.pharmacie.pharmacieapi.model.Produit;
import com.seydi.pharmacie.pharmacieapi.model.Stock;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CommandeMapper commandeMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private LigneCommandeMapper ligneCommandeMapper;

    @InjectMocks
    private CommandeService commandeService;

    @Test
    void listerCommandes_admin_retourneToutesLesCommandes() {

        doReturn(List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        )).when(authentication).getAuthorities();

        when(commandeRepository.findAll())
                .thenReturn(List.of());

        List<?> result = commandeService.listerCommandes(authentication);

        assertTrue(result.isEmpty());

        verify(authentication).getAuthorities();
        verify(commandeRepository).findAll();

        verify(commandeRepository, never())
                .findByClientId(anyLong());
    }

    @Test
    void listerCommandes_client_retourneUniquementSesCommandes() {

        doReturn(List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        )).when(authentication).getAuthorities();

        when(authentication.getName())
                .thenReturn("1");

        when(commandeRepository.findByClientId(1L))
                .thenReturn(List.of());

        List<?> result = commandeService.listerCommandes(authentication);

        assertTrue(result.isEmpty());

        verify(authentication).getAuthorities();
        verify(authentication).getName();

        verify(commandeRepository).findByClientId(1L);

        verify(commandeRepository, never())
                .findAll();
    }

    @Test
    void chercherCommandeParId_clientConsulteSaCommande_retourneCommande() {

        Client client = new Client();
        client.setId(2L);

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setClient(client);

        CommandeResponse response = mock(CommandeResponse.class);

        when(commandeRepository.findById(10L))
                .thenReturn(java.util.Optional.of(commande));

        doReturn(java.util.List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        )).when(authentication).getAuthorities();

        when(authentication.getName())
                .thenReturn("2");

        when(commandeMapper.toResponse(commande))
                .thenReturn(response);

        CommandeResponse result =
                commandeService.chercherCommandeParId(10L, authentication);

        assertEquals(response, result);

        verify(commandeRepository).findById(10L);
        verify(authentication).getAuthorities();
        verify(authentication).getName();
        verify(commandeMapper).toResponse(commande);
    }

    @Test
    void chercherCommandeParId_clientAccedeCommandeAutre_leveException() {

        Client proprietaire = new Client();
        proprietaire.setId(2L);

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setClient(proprietaire);

        when(commandeRepository.findById(10L))
                .thenReturn(java.util.Optional.of(commande));

        doReturn(java.util.List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        )).when(authentication).getAuthorities();

        when(authentication.getName())
                .thenReturn("1");

        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> commandeService.chercherCommandeParId(10L, authentication)
        );

        verify(commandeRepository).findById(10L);
        verify(authentication).getAuthorities();
        verify(authentication).getName();

        verify(commandeMapper, never())
                .toResponse(any(Commande.class));
    }

    @Test
    void chercherCommandeParId_adminConsulteCommande_retourneCommande() {

        Client proprietaire = new Client();
        proprietaire.setId(2L);

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setClient(proprietaire);

        CommandeResponse response = mock(CommandeResponse.class);

        when(commandeRepository.findById(10L))
                .thenReturn(java.util.Optional.of(commande));

        doReturn(java.util.List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        )).when(authentication).getAuthorities();

        when(commandeMapper.toResponse(commande))
                .thenReturn(response);

        CommandeResponse result =
                commandeService.chercherCommandeParId(10L, authentication);

        assertEquals(response, result);

        verify(commandeRepository).findById(10L);
        verify(authentication).getAuthorities();
        verify(commandeMapper).toResponse(commande);

        verify(authentication, never())
                .getName();
    }

    @Test
    void chercherCommandeParId_commandeInexistante_leveException() {

        when(commandeRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        CommandeNotFoundException exception = assertThrows(
                CommandeNotFoundException.class,
                () -> commandeService.chercherCommandeParId(999L, authentication)
        );

        assertEquals("Commande introuvable", exception.getMessage());

        verify(commandeRepository).findById(999L);

        verify(authentication, never())
                .getAuthorities();

        verify(authentication, never())
                .getName();

        verify(commandeMapper, never())
                .toResponse(any(Commande.class));
    }

    @Test
    void modifierStatutCommande_enAttenteVersConfirmee_modifieCommande() {

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setStatut(StatutCommande.EN_ATTENTE);

        UpdateStatutCommandeRequest request =
                mock(UpdateStatutCommandeRequest.class);

        when(request.getStatut())
                .thenReturn(StatutCommande.CONFIRMEE);

        CommandeResponse response = mock(CommandeResponse.class);

        when(commandeRepository.findById(10L))
                .thenReturn(java.util.Optional.of(commande));

        when(commandeRepository.save(commande))
                .thenReturn(commande);

        when(commandeMapper.toResponse(commande))
                .thenReturn(response);

        CommandeResponse result =
                commandeService.modifierStatutCommande(10L, request);

        assertEquals(response, result);

        verify(commandeRepository).findById(10L);
        verify(request).getStatut();
        verify(commandeMapper).updateEntity(commande, request);
        verify(commandeRepository).save(commande);
        verify(commandeMapper).toResponse(commande);
    }

    @Test
    void modifierStatutCommande_confirmeeVersPreparation_modifieCommande() {

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setStatut(StatutCommande.CONFIRMEE);

        UpdateStatutCommandeRequest request =
                mock(UpdateStatutCommandeRequest.class);

        when(request.getStatut())
                .thenReturn(StatutCommande.EN_PREPARATION);

        CommandeResponse response = mock(CommandeResponse.class);

        when(commandeRepository.findById(10L))
                .thenReturn(java.util.Optional.of(commande));

        when(commandeRepository.save(commande))
                .thenReturn(commande);

        when(commandeMapper.toResponse(commande))
                .thenReturn(response);

        CommandeResponse result =
                commandeService.modifierStatutCommande(10L, request);

        assertEquals(response, result);

        verify(commandeMapper).updateEntity(commande, request);
        verify(commandeRepository).save(commande);
        verify(commandeMapper).toResponse(commande);
    }

    @Test
    void modifierStatutCommande_preparationVersPrete_modifieCommande() {

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setStatut(StatutCommande.EN_PREPARATION);

        UpdateStatutCommandeRequest request =
                mock(UpdateStatutCommandeRequest.class);

        when(request.getStatut())
                .thenReturn(StatutCommande.PRETE);

        CommandeResponse response = mock(CommandeResponse.class);

        when(commandeRepository.findById(10L))
                .thenReturn(java.util.Optional.of(commande));

        when(commandeRepository.save(commande))
                .thenReturn(commande);

        when(commandeMapper.toResponse(commande))
                .thenReturn(response);

        CommandeResponse result =
                commandeService.modifierStatutCommande(10L, request);

        assertEquals(response, result);

        verify(commandeMapper).updateEntity(commande, request);
        verify(commandeRepository).save(commande);
        verify(commandeMapper).toResponse(commande);
    }

    @Test
    void modifierStatutCommande_preteVersLivree_modifieCommande() {

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setStatut(StatutCommande.PRETE);

        UpdateStatutCommandeRequest request =
                mock(UpdateStatutCommandeRequest.class);

        when(request.getStatut())
                .thenReturn(StatutCommande.LIVREE);

        CommandeResponse response = mock(CommandeResponse.class);

        when(commandeRepository.findById(10L))
                .thenReturn(java.util.Optional.of(commande));

        when(commandeRepository.save(commande))
                .thenReturn(commande);

        when(commandeMapper.toResponse(commande))
                .thenReturn(response);

        CommandeResponse result =
                commandeService.modifierStatutCommande(10L, request);

        assertEquals(response, result);

        verify(commandeMapper).updateEntity(commande, request);
        verify(commandeRepository).save(commande);
        verify(commandeMapper).toResponse(commande);
    }

    @Test
    void modifierStatutCommande_enAttenteVersPrete_leveException() {

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setStatut(StatutCommande.EN_ATTENTE);

        UpdateStatutCommandeRequest request =
                mock(UpdateStatutCommandeRequest.class);

        when(request.getStatut())
                .thenReturn(StatutCommande.PRETE);

        when(commandeRepository.findById(10L))
                .thenReturn(java.util.Optional.of(commande));

        CommandeTransitionException exception = assertThrows(
                CommandeTransitionException.class,
                () -> commandeService.modifierStatutCommande(10L, request)
        );

        assertEquals(
                "Transition de statut impossible : EN_ATTENTE → PRETE",
                exception.getMessage()
        );

        verify(commandeRepository).findById(10L);

        verify(commandeMapper, never())
                .updateEntity(any(Commande.class), any(UpdateStatutCommandeRequest.class));

        verify(commandeRepository, never())
                .save(any(Commande.class));

        verify(commandeMapper, never())
                .toResponse(any(Commande.class));
    }

    @Test
    void modifierStatutCommande_preparationVersAnnulee_leveException() {

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setStatut(StatutCommande.EN_PREPARATION);

        UpdateStatutCommandeRequest request =
                mock(UpdateStatutCommandeRequest.class);

        when(request.getStatut())
                .thenReturn(StatutCommande.ANNULEE);

        when(commandeRepository.findById(10L))
                .thenReturn(java.util.Optional.of(commande));

        assertThrows(
                CommandeTransitionException.class,
                () -> commandeService.modifierStatutCommande(10L, request)
        );

        verify(commandeMapper, never())
                .updateEntity(any(Commande.class), any(UpdateStatutCommandeRequest.class));

        verify(commandeRepository, never())
                .save(any(Commande.class));
    }

    @Test
    void modifierStatutCommande_livreeVersEnAttente_leveException() {

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setStatut(StatutCommande.LIVREE);

        UpdateStatutCommandeRequest request =
                mock(UpdateStatutCommandeRequest.class);

        when(request.getStatut())
                .thenReturn(StatutCommande.EN_ATTENTE);

        when(commandeRepository.findById(10L))
                .thenReturn(java.util.Optional.of(commande));

        CommandeTransitionException exception = assertThrows(
                CommandeTransitionException.class,
                () -> commandeService.modifierStatutCommande(10L, request)
        );

        assertEquals(
                "Impossible de modifier une commande LIVREE",
                exception.getMessage()
        );

        verify(commandeMapper, never())
                .updateEntity(any(Commande.class), any(UpdateStatutCommandeRequest.class));

        verify(commandeRepository, never())
                .save(any(Commande.class));
    }

    @Test
    void modifierStatutCommande_commandeInexistante_leveException() {

        UpdateStatutCommandeRequest request =
                mock(UpdateStatutCommandeRequest.class);

        when(commandeRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        CommandeNotFoundException exception = assertThrows(
                CommandeNotFoundException.class,
                () -> commandeService.modifierStatutCommande(999L, request)
        );

        assertEquals(
                "Commande introuvable",
                exception.getMessage()
        );

        verify(commandeRepository).findById(999L);

        verify(request, never())
                .getStatut();

        verify(commandeMapper, never())
                .updateEntity(any(Commande.class), any(UpdateStatutCommandeRequest.class));

        verify(commandeRepository, never())
                .save(any(Commande.class));
    }

    @Test
    void ajouterCommande_commandeValide_creeCommandeEtDiminueStock() {

        Client client = new Client();
        client.setId(1L);

        Produit produit = new Produit();
        produit.setId(1L);
        produit.setNom("Paracetamol");
        produit.setPrix(new BigDecimal("1500"));

        Stock stock = new Stock();
        stock.setQuantite(10);
        produit.setStock(stock);

        CreateLigneCommandeRequest ligneRequest =
                mock(CreateLigneCommandeRequest.class);

        when(ligneRequest.getProduitId())
                .thenReturn(1L);

        when(ligneRequest.getQuantite())
                .thenReturn(2);

        CreateCommandeRequest request =
                mock(CreateCommandeRequest.class);

        when(request.getLignes())
                .thenReturn(List.of(ligneRequest));

        Commande commande = new Commande();

        LigneCommande ligneCommande =
                mock(LigneCommande.class);

        when(ligneCommande.getQuantite())
                .thenReturn(2);

        when(authentication.getName())
                .thenReturn("1");

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        when(commandeMapper.toEntity(request))
                .thenReturn(commande);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        when(stockRepository.findByProduitId(1L))
                .thenReturn(Optional.of(stock));

        when(ligneCommandeMapper.toEntity(ligneRequest))
                .thenReturn(ligneCommande);

        when(commandeRepository.save(commande))
                .thenReturn(commande);

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeMapper.toResponse(commande))
                .thenReturn(response);

        CommandeResponse result =
                commandeService.ajouterCommande(request, authentication);

        assertEquals(response, result);

        assertEquals(client, commande.getClient());
        assertEquals(StatutCommande.EN_ATTENTE, commande.getStatut());
        assertEquals(new BigDecimal("3000"), commande.getTotalPrix());

        assertEquals(8, stock.getQuantite());

        assertEquals(1, commande.getLigneCommandes().size());
        assertTrue(commande.getLigneCommandes().contains(ligneCommande));

        verify(authentication).getName();
        verify(clientRepository).findById(1L);
        verify(commandeMapper).toEntity(request);
        verify(produitRepository).findById(1L);
        verify(stockRepository).findByProduitId(1L);
        verify(ligneCommandeMapper).toEntity(ligneRequest);
        verify(commandeRepository).save(commande);
        verify(commandeMapper).toResponse(commande);
    }

    @Test
    void ajouterCommande_clientInexistant_leveException() {

        when(authentication.getName())
                .thenReturn("999");

        when(clientRepository.findById(999L))
                .thenReturn(Optional.empty());

        CreateCommandeRequest request =
                mock(CreateCommandeRequest.class);

        assertThrows(
                ClientNotFoundException.class,
                () -> commandeService.ajouterCommande(request, authentication)
        );

        verify(authentication).getName();
        verify(clientRepository).findById(999L);

        verify(commandeMapper, never())
                .toEntity(any(CreateCommandeRequest.class));

        verify(commandeRepository, never())
                .save(any(Commande.class));
    }

    @Test
    void ajouterCommande_produitInexistant_leveException() {

        when(authentication.getName()).thenReturn("1");

        Client client = new Client();
        client.setId(1L);

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        CreateLigneCommandeRequest ligne = new CreateLigneCommandeRequest();
        ligne.setProduitId(999L);
        ligne.setQuantite(2);

        CreateCommandeRequest request = new CreateCommandeRequest();
        request.setLignes(List.of(ligne));

        when(commandeMapper.toEntity(request))
                .thenReturn(new Commande());

        when(produitRepository.findById(999L))
                .thenReturn(Optional.empty());


        assertThrows(
                ProduitNotFoundException.class,
                () -> commandeService.ajouterCommande(request, authentication)
        );
    }

    @Test
    void ajouterCommande_produitSansStock_leveException() {

        Client client = new Client();
        client.setId(1L);

        Produit produit = new Produit();
        produit.setId(1L);
        produit.setPrix(new BigDecimal("1500"));

        CreateLigneCommandeRequest ligneRequest =
                mock(CreateLigneCommandeRequest.class);

        when(ligneRequest.getProduitId())
                .thenReturn(1L);


        CreateCommandeRequest request =
                mock(CreateCommandeRequest.class);

        when(request.getLignes())
                .thenReturn(List.of(ligneRequest));

        Commande commande = new Commande();

        when(authentication.getName())
                .thenReturn("1");

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        when(commandeMapper.toEntity(request))
                .thenReturn(commande);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        when(stockRepository.findByProduitId(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                StockInsuffisantException.class,
                () -> commandeService.ajouterCommande(request, authentication)
        );

        verify(stockRepository).findByProduitId(1L);

        verify(ligneCommandeMapper, never())
                .toEntity(any(CreateLigneCommandeRequest.class));

        verify(commandeRepository, never())
                .save(any(Commande.class));
    }

    @Test
    void ajouterCommande_stockInsuffisant_leveException() {

        Client client = new Client();
        client.setId(1L);

        Produit produit = new Produit();
        produit.setId(1L);
        produit.setPrix(new BigDecimal("1500"));

        Stock stock = new Stock();
        stock.setQuantite(3);

        produit.setStock(stock);

        CreateLigneCommandeRequest ligneRequest =
                mock(CreateLigneCommandeRequest.class);

        when(ligneRequest.getProduitId())
                .thenReturn(1L);

        when(ligneRequest.getQuantite())
                .thenReturn(5);

        CreateCommandeRequest request =
                mock(CreateCommandeRequest.class);

        when(request.getLignes())
                .thenReturn(List.of(ligneRequest));

        Commande commande = new Commande();

        when(authentication.getName())
                .thenReturn("1");

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        when(commandeMapper.toEntity(request))
                .thenReturn(commande);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        when(stockRepository.findByProduitId(1L))
                .thenReturn(Optional.of(stock));

        assertThrows(
                StockInsuffisantException.class,
                () -> commandeService.ajouterCommande(request, authentication)
        );

        assertEquals(3, stock.getQuantite());

        verify(ligneCommandeMapper, never())
                .toEntity(any(CreateLigneCommandeRequest.class));

        verify(commandeRepository, never())
                .save(any(Commande.class));
    }

    @Test
    void ajouterCommande_memeProduitDeuxFois_leveException() {

        when(authentication.getName()).thenReturn("1");

        Client client = new Client();
        client.setId(1L);

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        Produit produit = new Produit();
        produit.setId(10L);
        produit.setPrix(new BigDecimal("1500"));

        Stock stock = new Stock();
        stock.setQuantite(10);

        produit.setStock(stock);

        CreateLigneCommandeRequest ligne1 = new CreateLigneCommandeRequest();
        ligne1.setProduitId(10L);
        ligne1.setQuantite(2);

        CreateLigneCommandeRequest ligne2 = new CreateLigneCommandeRequest();
        ligne2.setProduitId(10L);
        ligne2.setQuantite(3);

        CreateCommandeRequest request = new CreateCommandeRequest();
        request.setLignes(List.of(ligne1, ligne2));

        Commande commande = new Commande();

        LigneCommande ligneCommande = mock(LigneCommande.class);

        when(ligneCommande.getQuantite())
                .thenReturn(2);

        when(commandeMapper.toEntity(request))
                .thenReturn(commande);

        when(produitRepository.findById(10L))
                .thenReturn(Optional.of(produit));

        when(stockRepository.findByProduitId(10L))
                .thenReturn(Optional.of(stock));

        when(ligneCommandeMapper.toEntity(ligne1))
                .thenReturn(ligneCommande);

        assertThrows(
                ProduitDejaDansCommandeException.class,
                () -> commandeService.ajouterCommande(request, authentication)
        );

        verify(ligneCommandeMapper).toEntity(ligne1);
        verify(ligneCommandeMapper, never())
                .toEntity(ligne2);

        verify(commandeRepository, never())
                .save(any(Commande.class));
    }

    @Test
    void annulerCommande_clientProprietaire_annuleCommandeEtRestaureStock() {

        Client client = new Client();
        client.setId(1L);

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setClient(client);
        commande.setStatut(StatutCommande.EN_ATTENTE);

        Produit produit = new Produit();
        produit.setId(1L);

        Stock stock = new Stock();
        stock.setQuantite(8);
        produit.setStock(stock);

        LigneCommande ligne = mock(LigneCommande.class);

        when(ligne.getProduit())
                .thenReturn(produit);

        when(ligne.getQuantite())
                .thenReturn(2);

        commande.getLigneCommandes().add(ligne);

        doReturn(List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        )).when(authentication).getAuthorities();

        when(authentication.getName())
                .thenReturn("1");

        when(commandeRepository.findById(10L))
                .thenReturn(Optional.of(commande));

        when(commandeRepository.save(commande))
                .thenReturn(commande);

        CommandeResponse response = mock(CommandeResponse.class);

        when(commandeMapper.toResponse(commande))
                .thenReturn(response);

        CommandeResponse result =
                commandeService.annulerCommande(10L, authentication);

        assertEquals(response, result);
        assertEquals(StatutCommande.ANNULEE, commande.getStatut());
        assertEquals(10, stock.getQuantite());

        verify(commandeRepository).findById(10L);
        verify(commandeRepository).save(commande);
        verify(commandeMapper).toResponse(commande);
    }

    @Test
    void annulerCommande_clientCommandeAutre_leveException() {

        Client proprietaire = new Client();
        proprietaire.setId(2L);

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setClient(proprietaire);
        commande.setStatut(StatutCommande.EN_ATTENTE);

        doReturn(List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        )).when(authentication).getAuthorities();

        when(authentication.getName())
                .thenReturn("1");

        when(commandeRepository.findById(10L))
                .thenReturn(Optional.of(commande));

        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> commandeService.annulerCommande(10L, authentication)
        );

        verify(commandeRepository).findById(10L);

        verify(commandeRepository, never())
                .save(any(Commande.class));

        verify(commandeMapper, never())
                .toResponse(any(Commande.class));
    }

    @Test
    void annulerCommande_admin_annuleCommandeAvecSucces() {

        Client proprietaire = new Client();
        proprietaire.setId(2L);

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setClient(proprietaire);
        commande.setStatut(StatutCommande.EN_ATTENTE);

        Produit produit = new Produit();
        produit.setId(1L);

        Stock stock = new Stock();
        stock.setQuantite(5);
        produit.setStock(stock);

        LigneCommande ligne = mock(LigneCommande.class);

        when(ligne.getProduit())
                .thenReturn(produit);

        when(ligne.getQuantite())
                .thenReturn(3);

        commande.getLigneCommandes().add(ligne);

        doReturn(List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        )).when(authentication).getAuthorities();

        when(commandeRepository.findById(10L))
                .thenReturn(Optional.of(commande));

        when(commandeRepository.save(commande))
                .thenReturn(commande);

        CommandeResponse response = mock(CommandeResponse.class);

        when(commandeMapper.toResponse(commande))
                .thenReturn(response);

        CommandeResponse result =
                commandeService.annulerCommande(10L, authentication);

        assertEquals(response, result);
        assertEquals(StatutCommande.ANNULEE, commande.getStatut());
        assertEquals(8, stock.getQuantite());

        verify(authentication).getAuthorities();
        verify(authentication, never()).getName();
        verify(commandeRepository).save(commande);
        verify(commandeMapper).toResponse(commande);
    }

    @Test
    void annulerCommande_commandeLivree_leveException() {

        Client client = new Client();
        client.setId(1L);

        Commande commande = new Commande();
        commande.setId(10L);
        commande.setClient(client);
        commande.setStatut(StatutCommande.LIVREE);

        doReturn(List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        )).when(authentication).getAuthorities();

        when(authentication.getName())
                .thenReturn("1");

        when(commandeRepository.findById(10L))
                .thenReturn(Optional.of(commande));

        CommandeTransitionException exception = assertThrows(
                CommandeTransitionException.class,
                () -> commandeService.annulerCommande(10L, authentication)
        );

        assertEquals(
                "Impossible de modifier une commande LIVREE",
                exception.getMessage()
        );

        verify(commandeRepository, never())
                .save(any(Commande.class));

        verify(commandeMapper, never())
                .toResponse(any(Commande.class));
    }

    @Test
    void annulerCommande_commandeInexistante_leveException() {

        when(commandeRepository.findById(999L))
                .thenReturn(Optional.empty());

        CommandeNotFoundException exception = assertThrows(
                CommandeNotFoundException.class,
                () -> commandeService.annulerCommande(999L, authentication)
        );

        assertEquals(
                "Commande introuvable",
                exception.getMessage()
        );

        verify(commandeRepository).findById(999L);

        verify(authentication, never())
                .getAuthorities();

        verify(authentication, never())
                .getName();

        verify(commandeRepository, never())
                .save(any(Commande.class));
    }



}