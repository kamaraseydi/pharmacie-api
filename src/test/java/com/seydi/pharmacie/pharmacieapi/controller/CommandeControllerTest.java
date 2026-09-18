package com.seydi.pharmacie.pharmacieapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateLigneCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStatutCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.CommandeResponse;
import com.seydi.pharmacie.pharmacieapi.model.StatutCommande;
import com.seydi.pharmacie.pharmacieapi.service.CommandeService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class CommandeControllerTest {

    @Mock
    private CommandeService commandeService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {

        CommandeController commandeController =
                new CommandeController(commandeService);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(commandeController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }


    // =========================================================
    // POST /commandes
    // =========================================================

    @Test
    void ajouterCommande_donneesValides_doitRetourner201()
            throws Exception {

        CreateLigneCommandeRequest ligne =
                new CreateLigneCommandeRequest(1L, 2);

        CreateCommandeRequest request =
                new CreateCommandeRequest(List.of(ligne));

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeService.ajouterCommande(
                any(CreateCommandeRequest.class),
                eq(authentication)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/commandes")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .principal(authentication)
                )
                .andExpect(status().isCreated());

        verify(commandeService)
                .ajouterCommande(
                        any(CreateCommandeRequest.class),
                        eq(authentication)
                );
    }


    @Test
    void ajouterCommande_donneesInvalides_doitRetourner400()
            throws Exception {

        CreateCommandeRequest request =
                new CreateCommandeRequest();

        mockMvc.perform(
                        post("/commandes")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .principal(authentication)
                )
                .andExpect(status().isBadRequest());

        verify(commandeService, never())
                .ajouterCommande(
                        any(CreateCommandeRequest.class),
                        eq(authentication)
                );
    }


    // =========================================================
    // GET /commandes
    // =========================================================

    @Test
    void listerCommandes_doitRetourner200()
            throws Exception {

        when(commandeService.listerCommandes(authentication))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/commandes")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(commandeService)
                .listerCommandes(authentication);
    }


    // =========================================================
    // GET /commandes/{id}
    // =========================================================

    @Test
    void chercherCommandeParId_doitRetourner200()
            throws Exception {

        Long id = 1L;

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeService.chercherCommandeParId(
                id,
                authentication
        )).thenReturn(response);

        mockMvc.perform(
                        get("/commandes/{id}", id)
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(commandeService)
                .chercherCommandeParId(
                        id,
                        authentication
                );
    }


    // =========================================================
    // PUT /commandes/{id}/statut
    // =========================================================

    @Test
    void modifierStatutCommande_donneesValides_doitRetourner200()
            throws Exception {

        Long id = 1L;

        UpdateStatutCommandeRequest request =
                new UpdateStatutCommandeRequest(
                        StatutCommande.CONFIRMEE
                );

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeService.modifierStatutCommande(
                eq(id),
                any(UpdateStatutCommandeRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/commandes/{id}/statut", id)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(commandeService)
                .modifierStatutCommande(
                        eq(id),
                        any(UpdateStatutCommandeRequest.class)
                );
    }


    @Test
    void modifierStatutCommande_donneesInvalides_doitRetourner400()
            throws Exception {

        Long id = 1L;

        UpdateStatutCommandeRequest request =
                new UpdateStatutCommandeRequest();

        mockMvc.perform(
                        put("/commandes/{id}/statut", id)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(commandeService, never())
                .modifierStatutCommande(
                        eq(id),
                        any(UpdateStatutCommandeRequest.class)
                );
    }


    // =========================================================
    // PUT /commandes/{id}/annuler
    // =========================================================

    @Test
    void annulerCommande_doitRetourner200()
            throws Exception {

        Long id = 1L;

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeService.annulerCommande(
                id,
                authentication
        )).thenReturn(response);

        mockMvc.perform(
                        put("/commandes/{id}/annuler", id)
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(commandeService)
                .annulerCommande(
                        id,
                        authentication
                );
    }
}