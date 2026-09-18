package com.seydi.pharmacie.pharmacieapi.security;

import com.seydi.pharmacie.pharmacieapi.config.SecurityConfig;
import com.seydi.pharmacie.pharmacieapi.controller.CommandeController;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStatutCommandeRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.CommandeResponse;
import com.seydi.pharmacie.pharmacieapi.model.StatutCommande;
import com.seydi.pharmacie.pharmacieapi.service.CommandeService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommandeController.class)
@EnableWebSecurity
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "jwt.secret=une-cle-secrete-de-test-assez-longue-pour-hs256"
})
class CommandeSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommandeService commandeService;

    @MockitoBean
    private ClientDetailsService clientDetailsService;


    // =========================================================
    // POST /commandes
    // =========================================================

    @Test
    void postCommande_avecRoleClient_doitRetourner201()
            throws Exception {

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeService.ajouterCommande(
                any(CreateCommandeRequest.class),
                any()
        )).thenReturn(response);

        mockMvc.perform(
                post("/commandes")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "lignes": [
                                        {
                                            "produitId": 1,
                                            "quantite": 2
                                        }
                                    ]
                                }
                                """)
        ).andExpect(status().isCreated());

        verify(commandeService)
                .ajouterCommande(
                        any(CreateCommandeRequest.class),
                        any()
                );
    }


    @Test
    void postCommande_avecRoleAdmin_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                post("/commandes")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "lignes": [
                                        {
                                            "produitId": 1,
                                            "quantite": 2
                                        }
                                    ]
                                }
                                """)
        ).andExpect(status().isForbidden());

        verify(commandeService, never())
                .ajouterCommande(
                        any(CreateCommandeRequest.class),
                        any()
                );
    }


    @Test
    void postCommande_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                post("/commandes")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "lignes": [
                                        {
                                            "produitId": 1,
                                            "quantite": 2
                                        }
                                    ]
                                }
                                """)
        ).andExpect(status().isUnauthorized());

        verify(commandeService, never())
                .ajouterCommande(
                        any(CreateCommandeRequest.class),
                        any()
                );
    }


    // =========================================================
    // GET /commandes
    // =========================================================

    @Test
    void getCommandes_avecRoleClient_doitRetourner200()
            throws Exception {

        when(commandeService.listerCommandes(any()))
                .thenReturn(List.of());

        mockMvc.perform(
                get("/commandes")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isOk());

        verify(commandeService)
                .listerCommandes(any());
    }


    @Test
    void getCommandes_avecRoleAdmin_doitRetourner200()
            throws Exception {

        when(commandeService.listerCommandes(any()))
                .thenReturn(List.of());

        mockMvc.perform(
                get("/commandes")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isOk());

        verify(commandeService)
                .listerCommandes(any());
    }


    @Test
    void getCommandes_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                get("/commandes")
        ).andExpect(status().isUnauthorized());

        verify(commandeService, never())
                .listerCommandes(any());
    }


    // =========================================================
    // GET /commandes/{id}
    // =========================================================

    @Test
    void getCommandeParId_avecRoleClient_doitRetourner200()
            throws Exception {

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeService.chercherCommandeParId(
                eq(1L),
                any()
        )).thenReturn(response);

        mockMvc.perform(
                get("/commandes/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isOk());

        verify(commandeService)
                .chercherCommandeParId(
                        eq(1L),
                        any()
                );
    }


    @Test
    void getCommandeParId_avecRoleAdmin_doitRetourner200()
            throws Exception {

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeService.chercherCommandeParId(
                eq(1L),
                any()
        )).thenReturn(response);

        mockMvc.perform(
                get("/commandes/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isOk());

        verify(commandeService)
                .chercherCommandeParId(
                        eq(1L),
                        any()
                );
    }


    @Test
    void getCommandeParId_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                get("/commandes/1")
        ).andExpect(status().isUnauthorized());

        verify(commandeService, never())
                .chercherCommandeParId(
                        eq(1L),
                        any()
                );
    }


    // =========================================================
    // PUT /commandes/{id}/statut
    // =========================================================

    @Test
    void modifierStatutCommande_avecRoleAdmin_doitRetourner200()
            throws Exception {

        UpdateStatutCommandeRequest request =
                new UpdateStatutCommandeRequest(
                        StatutCommande.CONFIRMEE
                );

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeService.modifierStatutCommande(
                eq(1L),
                any(UpdateStatutCommandeRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                put("/commandes/1/statut")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "statut": "CONFIRMEE"
                                }
                                """)
        ).andExpect(status().isOk());

        verify(commandeService)
                .modifierStatutCommande(
                        eq(1L),
                        any(UpdateStatutCommandeRequest.class)
                );
    }


    @Test
    void modifierStatutCommande_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                put("/commandes/1/statut")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "statut": "CONFIRMEE"
                                }
                                """)
        ).andExpect(status().isForbidden());

        verify(commandeService, never())
                .modifierStatutCommande(
                        eq(1L),
                        any(UpdateStatutCommandeRequest.class)
                );
    }


    @Test
    void modifierStatutCommande_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                put("/commandes/1/statut")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "statut": "CONFIRMEE"
                                }
                                """)
        ).andExpect(status().isUnauthorized());

        verify(commandeService, never())
                .modifierStatutCommande(
                        eq(1L),
                        any(UpdateStatutCommandeRequest.class)
                );
    }


    // =========================================================
    // PUT /commandes/{id}/annuler
    // =========================================================

    @Test
    void annulerCommande_avecRoleClient_doitRetourner200()
            throws Exception {

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeService.annulerCommande(
                eq(1L),
                any()
        )).thenReturn(response);

        mockMvc.perform(
                put("/commandes/1/annuler")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isOk());

        verify(commandeService)
                .annulerCommande(
                        eq(1L),
                        any()
                );
    }


    @Test
    void annulerCommande_avecRoleAdmin_doitRetourner200()
            throws Exception {

        CommandeResponse response =
                mock(CommandeResponse.class);

        when(commandeService.annulerCommande(
                eq(1L),
                any()
        )).thenReturn(response);

        mockMvc.perform(
                put("/commandes/1/annuler")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isOk());

        verify(commandeService)
                .annulerCommande(
                        eq(1L),
                        any()
                );
    }


    @Test
    void annulerCommande_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                put("/commandes/1/annuler")
        ).andExpect(status().isUnauthorized());

        verify(commandeService, never())
                .annulerCommande(
                        eq(1L),
                        any()
                );
    }
}