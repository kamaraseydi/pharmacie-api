package com.seydi.pharmacie.pharmacieapi.service;

import com.seydi.pharmacie.pharmacieapi.dto.request.CreateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ProduitResponse;
import com.seydi.pharmacie.pharmacieapi.exception.FournisseurNotFoundException;
import com.seydi.pharmacie.pharmacieapi.exception.ProduitAlreadyExistsException;
import com.seydi.pharmacie.pharmacieapi.exception.ProduitHasStockException;
import com.seydi.pharmacie.pharmacieapi.exception.ProduitNotFoundException;
import com.seydi.pharmacie.pharmacieapi.mapper.ProduitMapper;
import com.seydi.pharmacie.pharmacieapi.model.Fournisseur;
import com.seydi.pharmacie.pharmacieapi.model.Produit;
import com.seydi.pharmacie.pharmacieapi.repository.FournisseurRepository;
import com.seydi.pharmacie.pharmacieapi.repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ProduitMapper produitMapper;

    @Mock
    private FournisseurRepository fournisseurRepository;

    @InjectMocks
    private ProduitService produitService;


    // =========================================================
    // LISTER PRODUITS
    // =========================================================

    @Test
    void listerProduits_doitRetournerLaListeDesProduits() {

        Produit produit1 = mock(Produit.class);
        Produit produit2 = mock(Produit.class);

        ProduitResponse response1 = mock(ProduitResponse.class);
        ProduitResponse response2 = mock(ProduitResponse.class);

        when(produitRepository.findAll())
                .thenReturn(List.of(produit1, produit2));

        when(produitMapper.toResponse(produit1))
                .thenReturn(response1);

        when(produitMapper.toResponse(produit2))
                .thenReturn(response2);

        List<ProduitResponse> result = produitService.listerProduits();

        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(produitRepository).findAll();
        verify(produitMapper).toResponse(produit1);
        verify(produitMapper).toResponse(produit2);
    }


    @Test
    void listerProduits_sansProduit_doitRetournerListeVide() {

        when(produitRepository.findAll())
                .thenReturn(List.of());

        List<ProduitResponse> result = produitService.listerProduits();

        assertTrue(result.isEmpty());

        verify(produitRepository).findAll();
        verifyNoInteractions(produitMapper);
    }


    // =========================================================
    // AJOUTER PRODUIT
    // =========================================================

    @Test
    void ajouterProduit_doitAjouterProduitAvecSucces() {

        CreateProduitRequest request = mock(CreateProduitRequest.class);

        Fournisseur fournisseur = mock(Fournisseur.class);
        Produit produit = mock(Produit.class);
        Produit produitSauvegarde = mock(Produit.class);
        ProduitResponse response = mock(ProduitResponse.class);

        List<Produit> produits = new ArrayList<>();

        when(request.getNom()).thenReturn("Paracetamol");
        when(request.getFournisseurId()).thenReturn(1L);

        when(produitRepository.existsByNom("Paracetamol"))
                .thenReturn(false);

        when(fournisseurRepository.findById(1L))
                .thenReturn(Optional.of(fournisseur));

        when(produitMapper.toEntity(request))
                .thenReturn(produit);

        when(fournisseur.getProduits())
                .thenReturn(produits);

        when(produitRepository.save(produit))
                .thenReturn(produitSauvegarde);

        when(produitMapper.toResponse(produitSauvegarde))
                .thenReturn(response);

        ProduitResponse result = produitService.ajouterProduit(request);

        assertEquals(response, result);

        verify(produitRepository).existsByNom("Paracetamol");
        verify(fournisseurRepository).findById(1L);
        verify(produitMapper).toEntity(request);

        verify(produit).setFournisseur(fournisseur);

        assertTrue(produits.contains(produit));

        verify(produitRepository).save(produit);
        verify(produitMapper).toResponse(produitSauvegarde);
    }


    @Test
    void ajouterProduit_avecNomDejaExistant_doitLeverException() {

        CreateProduitRequest request = mock(CreateProduitRequest.class);

        when(request.getNom()).thenReturn("Paracetamol");

        when(produitRepository.existsByNom("Paracetamol"))
                .thenReturn(true);

        assertThrows(
                ProduitAlreadyExistsException.class,
                () -> produitService.ajouterProduit(request)
        );

        verify(produitRepository).existsByNom("Paracetamol");

        verifyNoInteractions(fournisseurRepository);
        verifyNoInteractions(produitMapper);
    }


    @Test
    void ajouterProduit_avecFournisseurInexistant_doitLeverException() {

        CreateProduitRequest request = mock(CreateProduitRequest.class);

        when(request.getNom()).thenReturn("Paracetamol");
        when(request.getFournisseurId()).thenReturn(1L);

        when(produitRepository.existsByNom("Paracetamol"))
                .thenReturn(false);

        when(fournisseurRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                FournisseurNotFoundException.class,
                () -> produitService.ajouterProduit(request)
        );

        verify(produitRepository).existsByNom("Paracetamol");
        verify(fournisseurRepository).findById(1L);

        verifyNoInteractions(produitMapper);
        verify(produitRepository, never()).save(any());
    }


    // =========================================================
    // CHERCHER PRODUIT
    // =========================================================

    @Test
    void chercherProduitParId_doitRetournerLeProduit() {

        Long id = 1L;

        Produit produit = mock(Produit.class);
        ProduitResponse response = mock(ProduitResponse.class);

        when(produitRepository.findById(id))
                .thenReturn(Optional.of(produit));

        when(produitMapper.toResponse(produit))
                .thenReturn(response);

        ProduitResponse result = produitService.chercherProduitParId(id);

        assertEquals(response, result);

        verify(produitRepository).findById(id);
        verify(produitMapper).toResponse(produit);
    }


    @Test
    void chercherProduitParId_avecProduitInexistant_doitLeverException() {

        Long id = 99L;

        when(produitRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ProduitNotFoundException.class,
                () -> produitService.chercherProduitParId(id)
        );

        verify(produitRepository).findById(id);
        verifyNoInteractions(produitMapper);
    }


    // =========================================================
    // MODIFIER PRODUIT
    // =========================================================

    @Test
    void modifierProduit_avecProduitInexistant_doitLeverException() {

        Long id = 1L;
        UpdateProduitRequest request = mock(UpdateProduitRequest.class);

        when(produitRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ProduitNotFoundException.class,
                () -> produitService.modifierProduit(id, request)
        );

        verify(produitRepository).findById(id);

        verifyNoInteractions(fournisseurRepository);
        verifyNoInteractions(produitMapper);
    }


    @Test
    void modifierProduit_avecFournisseurInexistant_doitLeverException() {

        Long id = 1L;

        Produit produit = mock(Produit.class);
        UpdateProduitRequest request = mock(UpdateProduitRequest.class);

        when(produitRepository.findById(id))
                .thenReturn(Optional.of(produit));

        when(request.getFournisseurId())
                .thenReturn(2L);

        when(fournisseurRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                FournisseurNotFoundException.class,
                () -> produitService.modifierProduit(id, request)
        );

        verify(produitRepository).findById(id);
        verify(fournisseurRepository).findById(2L);

        verify(produitRepository, never()).existsByNom(any());
        verify(produitRepository, never()).save(any());
        verifyNoInteractions(produitMapper);
    }


    @Test
    void modifierProduit_avecMemeNom_neDoitPasVerifierLeDoublon() {

        Long id = 1L;

        Produit produit = mock(Produit.class);
        Fournisseur fournisseur = mock(Fournisseur.class);
        UpdateProduitRequest request = mock(UpdateProduitRequest.class);

        Produit produitSauvegarde = mock(Produit.class);
        ProduitResponse response = mock(ProduitResponse.class);

        when(produitRepository.findById(id))
                .thenReturn(Optional.of(produit));

        when(produit.getNom())
                .thenReturn("Paracetamol");

        when(request.getNom())
                .thenReturn("PARACETAMOL");

        when(request.getFournisseurId())
                .thenReturn(1L);

        when(produit.getFournisseur())
                .thenReturn(fournisseur);

        when(fournisseur.getId())
                .thenReturn(1L);

        when(fournisseurRepository.findById(1L))
                .thenReturn(Optional.of(fournisseur));

        when(produitRepository.save(produit))
                .thenReturn(produitSauvegarde);

        when(produitMapper.toResponse(produitSauvegarde))
                .thenReturn(response);

        ProduitResponse result =
                produitService.modifierProduit(id, request);

        assertEquals(response, result);

        verify(produitRepository).findById(id);
        verify(fournisseurRepository).findById(1L);

        verify(produitRepository, never()).existsByNom(any());

        verify(produitMapper).updateEntity(produit, request);
        verify(produitRepository).save(produit);
        verify(produitMapper).toResponse(produitSauvegarde);
    }


    @Test
    void modifierProduit_avecNouveauNomDejaExistant_doitLeverException() {

        Long id = 1L;

        Produit produit = mock(Produit.class);
        Fournisseur fournisseur = mock(Fournisseur.class);
        UpdateProduitRequest request = mock(UpdateProduitRequest.class);

        when(produitRepository.findById(id))
                .thenReturn(Optional.of(produit));

        when(request.getNom())
                .thenReturn("Ibuprofene");

        when(request.getFournisseurId())
                .thenReturn(1L);

        when(produit.getNom())
                .thenReturn("Paracetamol");

        when(fournisseurRepository.findById(1L))
                .thenReturn(Optional.of(fournisseur));

        when(produitRepository.existsByNom("Ibuprofene"))
                .thenReturn(true);

        assertThrows(
                ProduitAlreadyExistsException.class,
                () -> produitService.modifierProduit(id, request)
        );

        verify(produitRepository).findById(id);
        verify(fournisseurRepository).findById(1L);
        verify(produitRepository).existsByNom("Ibuprofene");

        verify(produitMapper, never()).updateEntity(any(), any());
        verify(produitRepository, never()).save(any());
    }


    @Test
    void modifierProduit_avecMemeFournisseur_doitModifierAvecSucces() {

        Long id = 1L;

        Produit produit = mock(Produit.class);
        Fournisseur fournisseur = mock(Fournisseur.class);
        UpdateProduitRequest request = mock(UpdateProduitRequest.class);

        Produit produitSauvegarde = mock(Produit.class);
        ProduitResponse response = mock(ProduitResponse.class);

        when(produitRepository.findById(id))
                .thenReturn(Optional.of(produit));

        when(produit.getNom())
                .thenReturn("Paracetamol");

        when(request.getNom())
                .thenReturn("NouveauNom");

        when(request.getFournisseurId())
                .thenReturn(1L);

        when(fournisseurRepository.findById(1L))
                .thenReturn(Optional.of(fournisseur));

        when(produitRepository.existsByNom("NouveauNom"))
                .thenReturn(false);

        when(produit.getFournisseur())
                .thenReturn(fournisseur);

        when(fournisseur.getId())
                .thenReturn(1L);

        when(produitRepository.save(produit))
                .thenReturn(produitSauvegarde);

        when(produitMapper.toResponse(produitSauvegarde))
                .thenReturn(response);

        ProduitResponse result =
                produitService.modifierProduit(id, request);

        assertEquals(response, result);

        verify(produitRepository).existsByNom("NouveauNom");
        verify(produitMapper).updateEntity(produit, request);
        verify(produitRepository).save(produit);

        verify(produit, never()).setFournisseur(any());
    }


    @Test
    void modifierProduit_avecNouveauFournisseur_doitMettreAJourLesRelations() {

        Long id = 1L;

        Produit produit = mock(Produit.class);

        Fournisseur ancienFournisseur = mock(Fournisseur.class);
        Fournisseur nouveauFournisseur = mock(Fournisseur.class);

        UpdateProduitRequest request = mock(UpdateProduitRequest.class);

        Produit produitSauvegarde = mock(Produit.class);
        ProduitResponse response = mock(ProduitResponse.class);

        List<Produit> anciensProduits = new ArrayList<>();
        List<Produit> nouveauxProduits = new ArrayList<>();

        anciensProduits.add(produit);

        when(produitRepository.findById(id))
                .thenReturn(Optional.of(produit));

        when(produit.getNom())
                .thenReturn("Paracetamol");

        when(request.getNom())
                .thenReturn("Paracetamol");

        when(request.getFournisseurId())
                .thenReturn(2L);

        when(produit.getFournisseur())
                .thenReturn(ancienFournisseur);

        when(ancienFournisseur.getId())
                .thenReturn(1L);

        when(ancienFournisseur.getProduits())
                .thenReturn(anciensProduits);

        when(nouveauFournisseur.getId())
                .thenReturn(2L);

        when(nouveauFournisseur.getProduits())
                .thenReturn(nouveauxProduits);

        when(fournisseurRepository.findById(2L))
                .thenReturn(Optional.of(nouveauFournisseur));

        when(produitRepository.save(produit))
                .thenReturn(produitSauvegarde);

        when(produitMapper.toResponse(produitSauvegarde))
                .thenReturn(response);

        ProduitResponse result =
                produitService.modifierProduit(id, request);

        assertEquals(response, result);

        assertFalse(anciensProduits.contains(produit));
        assertTrue(nouveauxProduits.contains(produit));

        verify(produit).setFournisseur(nouveauFournisseur);
        verify(produitMapper).updateEntity(produit, request);
        verify(produitRepository).save(produit);
    }


    // =========================================================
    // SUPPRIMER PRODUIT
    // =========================================================

    @Test
    void supprimerProduit_avecProduitInexistant_doitLeverException() {

        Long id = 1L;

        when(produitRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ProduitNotFoundException.class,
                () -> produitService.supprimerProduit(id)
        );

        verify(produitRepository).findById(id);

        verify(produitRepository, never()).delete(any());
    }


    @Test
    void supprimerProduit_avecStock_doitLeverException() {

        Long id = 1L;

        Produit produit = mock(Produit.class);

        com.seydi.pharmacie.pharmacieapi.model.Stock stock =
                mock(com.seydi.pharmacie.pharmacieapi.model.Stock.class);

        when(produitRepository.findById(id))
                .thenReturn(Optional.of(produit));

        when(produit.getStock())
                .thenReturn(stock);

        assertThrows(
                ProduitHasStockException.class,
                () -> produitService.supprimerProduit(id)
        );

        verify(produitRepository).findById(id);

        verify(produitRepository, never()).delete(any());
        verify(produit, never()).setFournisseur(any());
    }


    @Test
    void supprimerProduit_sansStock_doitSupprimerProduitEtRompreRelation() {

        Long id = 1L;

        Produit produit = mock(Produit.class);
        Fournisseur fournisseur = mock(Fournisseur.class);

        List<Produit> produits = new ArrayList<>();
        produits.add(produit);

        when(produitRepository.findById(id))
                .thenReturn(Optional.of(produit));

        when(produit.getStock())
                .thenReturn(null);

        when(produit.getFournisseur())
                .thenReturn(fournisseur);

        when(fournisseur.getProduits())
                .thenReturn(produits);

        produitService.supprimerProduit(id);

        assertFalse(produits.contains(produit));

        verify(produit).setFournisseur(null);
        verify(produitRepository).delete(produit);
    }
}