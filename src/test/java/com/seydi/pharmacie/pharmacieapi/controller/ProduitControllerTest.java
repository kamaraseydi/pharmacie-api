package com.seydi.pharmacie.pharmacieapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ProduitResponse;
import com.seydi.pharmacie.pharmacieapi.service.ProduitService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ProduitControllerTest {

    @Mock
    private ProduitService produitService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {

        ProduitController produitController =
                new ProduitController(produitService);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(produitController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }


    // =========================================================
    // POST /produits
    // =========================================================

    @Test
    void ajouterProduit_donneesValides_doitRetourner201()
            throws Exception {

        CreateProduitRequest request =
                new CreateProduitRequest(
                        "Paracetamol",
                        "Antalgique pour douleurs",
                        new BigDecimal("1500.00")
                );

        request.setFournisseurId(1L);

        ProduitResponse response =
                mock(ProduitResponse.class);

        when(produitService.ajouterProduit(
                any(CreateProduitRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/produits")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        verify(produitService)
                .ajouterProduit(any(CreateProduitRequest.class));
    }


    @Test
    void ajouterProduit_donneesInvalides_doitRetourner400()
            throws Exception {

        CreateProduitRequest request =
                new CreateProduitRequest();

        request.setNom("");

        mockMvc.perform(
                        post("/produits")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(produitService, never())
                .ajouterProduit(any(CreateProduitRequest.class));
    }


    // =========================================================
    // GET /produits
    // =========================================================

    @Test
    void listerProduits_doitRetourner200()
            throws Exception {

        when(produitService.listerProduits())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/produits")
                )
                .andExpect(status().isOk());

        verify(produitService)
                .listerProduits();
    }


    // =========================================================
    // GET /produits/{id}
    // =========================================================

    @Test
    void chercherProduitParId_doitRetourner200()
            throws Exception {

        Long id = 1L;

        ProduitResponse response =
                mock(ProduitResponse.class);

        when(produitService.chercherProduitParId(id))
                .thenReturn(response);

        mockMvc.perform(
                        get("/produits/{id}", id)
                )
                .andExpect(status().isOk());

        verify(produitService)
                .chercherProduitParId(id);
    }


    // =========================================================
    // PUT /produits/{id}
    // =========================================================

    @Test
    void modifierProduit_donneesValides_doitRetourner200()
            throws Exception {

        Long id = 1L;

        UpdateProduitRequest request =
                new UpdateProduitRequest(
                        "Paracetamol",
                        "Antalgique pour douleurs",
                        new BigDecimal("1800.00")
                );

        request.setFournisseurId(1L);

        ProduitResponse response =
                mock(ProduitResponse.class);

        when(produitService.modifierProduit(
                eq(id),
                any(UpdateProduitRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/produits/{id}", id)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(produitService)
                .modifierProduit(
                        eq(id),
                        any(UpdateProduitRequest.class)
                );
    }


    @Test
    void modifierProduit_donneesInvalides_doitRetourner400()
            throws Exception {

        Long id = 1L;

        UpdateProduitRequest request =
                new UpdateProduitRequest();

        request.setNom("");

        mockMvc.perform(
                        put("/produits/{id}", id)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(produitService, never())
                .modifierProduit(
                        eq(id),
                        any(UpdateProduitRequest.class)
                );
    }


    // =========================================================
    // DELETE /produits/{id}
    // =========================================================

    @Test
    void supprimerProduit_doitRetourner204()
            throws Exception {

        Long id = 1L;

        doNothing()
                .when(produitService)
                .supprimerProduit(id);

        mockMvc.perform(
                        delete("/produits/{id}", id)
                )
                .andExpect(status().isNoContent());

        verify(produitService)
                .supprimerProduit(id);
    }
}