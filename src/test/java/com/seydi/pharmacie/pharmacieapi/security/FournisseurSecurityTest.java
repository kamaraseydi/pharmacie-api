package com.seydi.pharmacie.pharmacieapi.security;

import com.seydi.pharmacie.pharmacieapi.config.SecurityConfig;
import com.seydi.pharmacie.pharmacieapi.controller.FournisseurController;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateFournisseurRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.FournisseurResponse;
import com.seydi.pharmacie.pharmacieapi.service.FournisseurService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FournisseurController.class)
@EnableWebSecurity
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "jwt.secret=une-cle-secrete-de-test-assez-longue-pour-hs256"
})
class FournisseurSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FournisseurService fournisseurService;

    @MockitoBean
    private ClientDetailsService clientDetailsService;


    // =========================================================
    // POST /fournisseurs
    // =========================================================

    @Test
    void postFournisseur_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                post("/fournisseurs")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Pharma Plus",
                                    "adresse": "Dakar",
                                    "telephone": "771234567",
                                    "email": "contact@pharmaplus.com"
                                }
                                """)
        ).andExpect(status().isUnauthorized());

        verify(fournisseurService, never())
                .ajouterFournisseur(any(CreateFournisseurRequest.class));
    }


    @Test
    void postFournisseur_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                post("/fournisseurs")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Pharma Plus",
                                    "adresse": "Dakar",
                                    "telephone": "771234567",
                                    "email": "contact@pharmaplus.com"
                                }
                                """)
        ).andExpect(status().isForbidden());

        verify(fournisseurService, never())
                .ajouterFournisseur(any(CreateFournisseurRequest.class));
    }


    @Test
    void postFournisseur_avecRoleAdmin_doitRetourner201()
            throws Exception {

        FournisseurResponse response =
                mock(FournisseurResponse.class);

        when(fournisseurService.ajouterFournisseur(
                any(CreateFournisseurRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                post("/fournisseurs")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Pharma Plus",
                                    "adresse": "Dakar",
                                    "telephone": "771234567",
                                    "email": "contact@pharmaplus.com"
                                }
                                """)
        ).andExpect(status().isCreated());

        verify(fournisseurService)
                .ajouterFournisseur(
                        any(CreateFournisseurRequest.class)
                );
    }


    // =========================================================
    // GET /fournisseurs
    // =========================================================

    @Test
    void getFournisseurs_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                get("/fournisseurs")
        ).andExpect(status().isUnauthorized());

        verify(fournisseurService, never())
                .listerFournisseurs();
    }


    @Test
    void getFournisseurs_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                get("/fournisseurs")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isForbidden());

        verify(fournisseurService, never())
                .listerFournisseurs();
    }


    @Test
    void getFournisseurs_avecRoleAdmin_doitRetourner200()
            throws Exception {

        when(fournisseurService.listerFournisseurs())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/fournisseurs")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isOk());

        verify(fournisseurService)
                .listerFournisseurs();
    }


    // =========================================================
    // GET /fournisseurs/{id}
    // =========================================================

    @Test
    void getFournisseurParId_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                get("/fournisseurs/1")
        ).andExpect(status().isUnauthorized());

        verify(fournisseurService, never())
                .chercherFournisseurParId(1L);
    }


    @Test
    void getFournisseurParId_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                get("/fournisseurs/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isForbidden());

        verify(fournisseurService, never())
                .chercherFournisseurParId(1L);
    }


    @Test
    void getFournisseurParId_avecRoleAdmin_doitRetourner200()
            throws Exception {

        FournisseurResponse response =
                mock(FournisseurResponse.class);

        when(fournisseurService.chercherFournisseurParId(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/fournisseurs/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isOk());

        verify(fournisseurService)
                .chercherFournisseurParId(1L);
    }


    // =========================================================
    // PUT /fournisseurs/{id}
    // =========================================================

    @Test
    void putFournisseur_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                put("/fournisseurs/1")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Pharma Plus",
                                    "adresse": "Dakar",
                                    "telephone": "771234567",
                                    "email": "contact@pharmaplus.com"
                                }
                                """)
        ).andExpect(status().isUnauthorized());

        verify(fournisseurService, never())
                .modifierFournisseur(
                        eq(1L),
                        any(UpdateFournisseurRequest.class)
                );
    }


    @Test
    void putFournisseur_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                put("/fournisseurs/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Pharma Plus",
                                    "adresse": "Dakar",
                                    "telephone": "771234567",
                                    "email": "contact@pharmaplus.com"
                                }
                                """)
        ).andExpect(status().isForbidden());

        verify(fournisseurService, never())
                .modifierFournisseur(
                        eq(1L),
                        any(UpdateFournisseurRequest.class)
                );
    }


    @Test
    void putFournisseur_avecRoleAdmin_doitRetourner200()
            throws Exception {

        FournisseurResponse response =
                mock(FournisseurResponse.class);

        when(fournisseurService.modifierFournisseur(
                eq(1L),
                any(UpdateFournisseurRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                put("/fournisseurs/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Pharma Plus",
                                    "adresse": "Dakar",
                                    "telephone": "771234567",
                                    "email": "contact@pharmaplus.com"
                                }
                                """)
        ).andExpect(status().isOk());

        verify(fournisseurService)
                .modifierFournisseur(
                        eq(1L),
                        any(UpdateFournisseurRequest.class)
                );
    }


    // =========================================================
    // DELETE /fournisseurs/{id}
    // =========================================================

    @Test
    void deleteFournisseur_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                delete("/fournisseurs/1")
        ).andExpect(status().isUnauthorized());

        verify(fournisseurService, never())
                .supprimerFournisseur(1L);
    }


    @Test
    void deleteFournisseur_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                delete("/fournisseurs/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isForbidden());

        verify(fournisseurService, never())
                .supprimerFournisseur(1L);
    }


    @Test
    void deleteFournisseur_avecRoleAdmin_doitRetourner204()
            throws Exception {

        doNothing()
                .when(fournisseurService)
                .supprimerFournisseur(1L);

        mockMvc.perform(
                delete("/fournisseurs/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isNoContent());

        verify(fournisseurService)
                .supprimerFournisseur(1L);
    }
}