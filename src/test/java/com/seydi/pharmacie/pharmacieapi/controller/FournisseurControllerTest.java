package com.seydi.pharmacie.pharmacieapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.FournisseurResponse;
import com.seydi.pharmacie.pharmacieapi.service.FournisseurService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FournisseurControllerTest {

    @Mock
    private FournisseurService fournisseurService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {

        FournisseurController fournisseurController =
                new FournisseurController(fournisseurService);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(fournisseurController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }


    // =========================================================
    // POST /fournisseurs
    // =========================================================

    @Test
    void ajouterFournisseur_donneesValides_doitRetourner201() throws Exception {

        CreateFournisseurRequest request =
                new CreateFournisseurRequest(
                        "Pharma Plus",
                        "Dakar",
                        "771234567",
                        "contact@pharmaplus.com"
                );

        FournisseurResponse response = new FournisseurResponse();
        response.setId(1L);
        response.setNom("Pharma Plus");
        response.setAdresse("Dakar");
        response.setTelephone("771234567");
        response.setEmail("contact@pharmaplus.com");

        when(fournisseurService.ajouterFournisseur(any(CreateFournisseurRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Pharma Plus"))
                .andExpect(jsonPath("$.adresse").value("Dakar"))
                .andExpect(jsonPath("$.telephone").value("771234567"))
                .andExpect(jsonPath("$.email").value("contact@pharmaplus.com"));
    }


    @Test
    void ajouterFournisseur_donneesInvalides_doitRetourner400()
            throws Exception {

        CreateFournisseurRequest request =
                new CreateFournisseurRequest();

        request.setNom("");

        mockMvc.perform(
                        post("/fournisseurs")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(fournisseurService, never())
                .ajouterFournisseur(any(CreateFournisseurRequest.class));
    }


    // =========================================================
    // GET /fournisseurs
    // =========================================================

    @Test
    void listerFournisseurs_doitRetourner200()
            throws Exception {

        when(fournisseurService.listerFournisseurs())
                .thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/fournisseurs")
                )
                .andExpect(status().isOk());

        verify(fournisseurService)
                .listerFournisseurs();
    }


    // =========================================================
    // GET /fournisseurs/{id}
    // =========================================================

    @Test
    void chercherFournisseurParId_doitRetourner200()
            throws Exception {

        Long id = 1L;

        FournisseurResponse response =
                mock(FournisseurResponse.class);

        when(fournisseurService.chercherFournisseurParId(id))
                .thenReturn(response);

        mockMvc.perform(
                        get("/fournisseurs/{id}", id)
                )
                .andExpect(status().isOk());

        verify(fournisseurService)
                .chercherFournisseurParId(id);
    }


    // =========================================================
    // PUT /fournisseurs/{id}
    // =========================================================

    @Test
    void modifierFournisseur_donneesValides_doitRetourner200() throws Exception {

        UpdateFournisseurRequest request =
                new UpdateFournisseurRequest(
                        "Pharma Plus",
                        "Dakar",
                        "771234567",
                        "contact@pharmaplus.com"
                );

        FournisseurResponse response = new FournisseurResponse();
        response.setId(1L);
        response.setNom("Pharma Plus");
        response.setAdresse("Dakar");
        response.setTelephone("771234567");
        response.setEmail("contact@pharmaplus.com");

        when(fournisseurService.modifierFournisseur(
                eq(1L),
                any(UpdateFournisseurRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/fournisseurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Pharma Plus"))
                .andExpect(jsonPath("$.adresse").value("Dakar"))
                .andExpect(jsonPath("$.telephone").value("771234567"))
                .andExpect(jsonPath("$.email").value("contact@pharmaplus.com"));
    }


    @Test
    void modifierFournisseur_donneesInvalides_doitRetourner400()
            throws Exception {

        Long id = 1L;

        UpdateFournisseurRequest request =
                new UpdateFournisseurRequest();

        request.setNom("");

        mockMvc.perform(
                        put("/fournisseurs/{id}", id)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(fournisseurService, never())
                .modifierFournisseur(
                        eq(id),
                        any(UpdateFournisseurRequest.class)
                );
    }


    // =========================================================
    // DELETE /fournisseurs/{id}
    // =========================================================

    @Test
    void supprimerFournisseur_doitRetourner204()
            throws Exception {

        Long id = 1L;

        doNothing()
                .when(fournisseurService)
                .supprimerFournisseur(id);

        mockMvc.perform(
                        delete("/fournisseurs/{id}", id)
                )
                .andExpect(status().isNoContent());

        verify(fournisseurService)
                .supprimerFournisseur(id);
    }
}