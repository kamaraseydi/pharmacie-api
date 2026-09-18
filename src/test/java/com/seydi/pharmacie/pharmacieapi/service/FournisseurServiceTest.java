package com.seydi.pharmacie.pharmacieapi.service;

import com.seydi.pharmacie.pharmacieapi.dto.request.CreateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.FournisseurResponse;
import com.seydi.pharmacie.pharmacieapi.exception.FournisseurAlreadyExistsException;
import com.seydi.pharmacie.pharmacieapi.exception.FournisseurHasProductsException;
import com.seydi.pharmacie.pharmacieapi.exception.FournisseurNotFoundException;
import com.seydi.pharmacie.pharmacieapi.mapper.FournisseurMapper;
import com.seydi.pharmacie.pharmacieapi.model.Fournisseur;
import com.seydi.pharmacie.pharmacieapi.model.Produit;
import com.seydi.pharmacie.pharmacieapi.repository.FournisseurRepository;
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
class FournisseurServiceTest {

    @Mock
    private FournisseurRepository fournisseurRepository;

    @Mock
    private FournisseurMapper fournisseurMapper;

    @InjectMocks
    private FournisseurService fournisseurService;


    // =========================================================
    // AJOUTER FOURNISSEUR
    // =========================================================

    @Test
    void ajouterFournisseur_doitAjouterAvecSucces() {

        CreateFournisseurRequest request = mock(CreateFournisseurRequest.class);

        Fournisseur fournisseur = mock(Fournisseur.class);
        Fournisseur fournisseurSauvegarde = mock(Fournisseur.class);
        FournisseurResponse response = mock(FournisseurResponse.class);

        when(request.getNom())
                .thenReturn("Pharma Distribution");

        when(fournisseurRepository.existsByNom("Pharma Distribution"))
                .thenReturn(false);

        when(fournisseurMapper.toEntity(request))
                .thenReturn(fournisseur);

        when(fournisseurRepository.save(fournisseur))
                .thenReturn(fournisseurSauvegarde);

        when(fournisseurMapper.toResponse(fournisseurSauvegarde))
                .thenReturn(response);

        FournisseurResponse result =
                fournisseurService.ajouterFournisseur(request);

        assertEquals(response, result);

        verify(fournisseurRepository)
                .existsByNom("Pharma Distribution");

        verify(fournisseurMapper)
                .toEntity(request);

        verify(fournisseurRepository)
                .save(fournisseur);

        verify(fournisseurMapper)
                .toResponse(fournisseurSauvegarde);
    }


    @Test
    void ajouterFournisseur_avecNomDejaExistant_doitLeverException() {

        CreateFournisseurRequest request = mock(CreateFournisseurRequest.class);

        when(request.getNom())
                .thenReturn("Pharma Distribution");

        when(fournisseurRepository.existsByNom("Pharma Distribution"))
                .thenReturn(true);

        assertThrows(
                FournisseurAlreadyExistsException.class,
                () -> fournisseurService.ajouterFournisseur(request)
        );

        verify(fournisseurRepository)
                .existsByNom("Pharma Distribution");

        verifyNoInteractions(fournisseurMapper);

        verify(fournisseurRepository, never())
                .save(any());
    }


    // =========================================================
    // LISTER FOURNISSEURS
    // =========================================================

    @Test
    void listerFournisseurs_doitRetournerLaListe() {

        Fournisseur fournisseur1 = mock(Fournisseur.class);
        Fournisseur fournisseur2 = mock(Fournisseur.class);

        FournisseurResponse response1 = mock(FournisseurResponse.class);
        FournisseurResponse response2 = mock(FournisseurResponse.class);

        when(fournisseurRepository.findAll())
                .thenReturn(List.of(fournisseur1, fournisseur2));

        when(fournisseurMapper.toResponse(fournisseur1))
                .thenReturn(response1);

        when(fournisseurMapper.toResponse(fournisseur2))
                .thenReturn(response2);

        List<FournisseurResponse> result =
                fournisseurService.listerFournisseurs();

        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(fournisseurRepository)
                .findAll();

        verify(fournisseurMapper)
                .toResponse(fournisseur1);

        verify(fournisseurMapper)
                .toResponse(fournisseur2);
    }


    @Test
    void listerFournisseurs_sansFournisseur_doitRetournerListeVide() {

        when(fournisseurRepository.findAll())
                .thenReturn(List.of());

        List<FournisseurResponse> result =
                fournisseurService.listerFournisseurs();

        assertTrue(result.isEmpty());

        verify(fournisseurRepository)
                .findAll();

        verifyNoInteractions(fournisseurMapper);
    }


    // =========================================================
    // CHERCHER FOURNISSEUR PAR ID
    // =========================================================

    @Test
    void chercherFournisseurParId_doitRetournerLeFournisseur() {

        Long id = 1L;

        Fournisseur fournisseur = mock(Fournisseur.class);
        FournisseurResponse response = mock(FournisseurResponse.class);

        when(fournisseurRepository.findById(id))
                .thenReturn(Optional.of(fournisseur));

        when(fournisseurMapper.toResponse(fournisseur))
                .thenReturn(response);

        FournisseurResponse result =
                fournisseurService.chercherFournisseurParId(id);

        assertEquals(response, result);

        verify(fournisseurRepository)
                .findById(id);

        verify(fournisseurMapper)
                .toResponse(fournisseur);
    }


    @Test
    void chercherFournisseurParId_fournisseurInexistant_doitLeverException() {

        Long id = 99L;

        when(fournisseurRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                FournisseurNotFoundException.class,
                () -> fournisseurService.chercherFournisseurParId(id)
        );

        verify(fournisseurRepository)
                .findById(id);

        verifyNoInteractions(fournisseurMapper);
    }


    // =========================================================
    // MODIFIER FOURNISSEUR
    // =========================================================

    @Test
    void modifierFournisseur_fournisseurInexistant_doitLeverException() {

        Long id = 1L;

        UpdateFournisseurRequest request =
                mock(UpdateFournisseurRequest.class);

        when(fournisseurRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                FournisseurNotFoundException.class,
                () -> fournisseurService.modifierFournisseur(id, request)
        );

        verify(fournisseurRepository)
                .findById(id);

        verifyNoInteractions(fournisseurMapper);

        verify(fournisseurRepository, never())
                .existsByNom(any());

        verify(fournisseurRepository, never())
                .save(any());
    }


    @Test
    void modifierFournisseur_avecMemeNom_neDoitPasVerifierDoublon() {

        Long id = 1L;

        Fournisseur fournisseur =
                mock(Fournisseur.class);

        UpdateFournisseurRequest request =
                mock(UpdateFournisseurRequest.class);

        Fournisseur fournisseurSauvegarde =
                mock(Fournisseur.class);

        FournisseurResponse response =
                mock(FournisseurResponse.class);

        when(fournisseurRepository.findById(id))
                .thenReturn(Optional.of(fournisseur));

        when(fournisseur.getNom())
                .thenReturn("Pfizer");

        when(request.getNom())
                .thenReturn("PFIZER");

        when(fournisseurRepository.save(fournisseur))
                .thenReturn(fournisseurSauvegarde);

        when(fournisseurMapper.toResponse(fournisseurSauvegarde))
                .thenReturn(response);

        FournisseurResponse result =
                fournisseurService.modifierFournisseur(id, request);

        assertEquals(response, result);

        verify(fournisseurRepository)
                .findById(id);

        verify(fournisseurRepository, never())
                .existsByNom(any());

        verify(fournisseurMapper)
                .updateEntity(fournisseur, request);

        verify(fournisseurRepository)
                .save(fournisseur);

        verify(fournisseurMapper)
                .toResponse(fournisseurSauvegarde);
    }


    @Test
    void modifierFournisseur_avecNouveauNomDejaExistant_doitLeverException() {

        Long id = 1L;

        Fournisseur fournisseur =
                mock(Fournisseur.class);

        UpdateFournisseurRequest request =
                mock(UpdateFournisseurRequest.class);

        when(fournisseurRepository.findById(id))
                .thenReturn(Optional.of(fournisseur));

        when(fournisseur.getNom())
                .thenReturn("Pfizer");

        when(request.getNom())
                .thenReturn("Sanofi");

        when(fournisseurRepository.existsByNom("Sanofi"))
                .thenReturn(true);

        assertThrows(
                FournisseurAlreadyExistsException.class,
                () -> fournisseurService.modifierFournisseur(id, request)
        );

        verify(fournisseurRepository)
                .findById(id);

        verify(fournisseurRepository)
                .existsByNom("Sanofi");

        verify(fournisseurMapper, never())
                .updateEntity(any(), any());

        verify(fournisseurRepository, never())
                .save(any());
    }


    @Test
    void modifierFournisseur_avecNouveauNomDisponible_doitModifierAvecSucces() {

        Long id = 1L;

        Fournisseur fournisseur =
                mock(Fournisseur.class);

        UpdateFournisseurRequest request =
                mock(UpdateFournisseurRequest.class);

        Fournisseur fournisseurSauvegarde =
                mock(Fournisseur.class);

        FournisseurResponse response =
                mock(FournisseurResponse.class);

        when(fournisseurRepository.findById(id))
                .thenReturn(Optional.of(fournisseur));

        when(fournisseur.getNom())
                .thenReturn("Pfizer");

        when(request.getNom())
                .thenReturn("Sanofi");

        when(fournisseurRepository.existsByNom("Sanofi"))
                .thenReturn(false);

        when(fournisseurRepository.save(fournisseur))
                .thenReturn(fournisseurSauvegarde);

        when(fournisseurMapper.toResponse(fournisseurSauvegarde))
                .thenReturn(response);

        FournisseurResponse result =
                fournisseurService.modifierFournisseur(id, request);

        assertEquals(response, result);

        verify(fournisseurRepository)
                .findById(id);

        verify(fournisseurRepository)
                .existsByNom("Sanofi");

        verify(fournisseurMapper)
                .updateEntity(fournisseur, request);

        verify(fournisseurRepository)
                .save(fournisseur);

        verify(fournisseurMapper)
                .toResponse(fournisseurSauvegarde);
    }


    // =========================================================
    // SUPPRIMER FOURNISSEUR
    // =========================================================

    @Test
    void supprimerFournisseur_fournisseurInexistant_doitLeverException() {

        Long id = 1L;

        when(fournisseurRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                FournisseurNotFoundException.class,
                () -> fournisseurService.supprimerFournisseur(id)
        );

        verify(fournisseurRepository)
                .findById(id);

        verify(fournisseurRepository, never())
                .delete(any());
    }


    @Test
    void supprimerFournisseur_avecProduits_doitLeverException() {

        Long id = 1L;

        Fournisseur fournisseur =
                mock(Fournisseur.class);

        Produit produit =
                mock(Produit.class);

        List<Produit> produits =
                new ArrayList<>();

        produits.add(produit);

        when(fournisseurRepository.findById(id))
                .thenReturn(Optional.of(fournisseur));

        when(fournisseur.getProduits())
                .thenReturn(produits);

        assertThrows(
                FournisseurHasProductsException.class,
                () -> fournisseurService.supprimerFournisseur(id)
        );

        verify(fournisseurRepository)
                .findById(id);

        verify(fournisseur)
                .getProduits();

        verify(fournisseurRepository, never())
                .delete(any());
    }


    @Test
    void supprimerFournisseur_sansProduits_doitSupprimerAvecSucces() {

        Long id = 1L;

        Fournisseur fournisseur =
                mock(Fournisseur.class);

        when(fournisseurRepository.findById(id))
                .thenReturn(Optional.of(fournisseur));

        when(fournisseur.getProduits())
                .thenReturn(new ArrayList<>());

        fournisseurService.supprimerFournisseur(id);

        verify(fournisseurRepository)
                .findById(id);

        verify(fournisseurRepository)
                .delete(fournisseur);
    }
}