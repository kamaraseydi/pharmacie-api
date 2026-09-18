package com.seydi.pharmacie.pharmacieapi.security;

import com.seydi.pharmacie.pharmacieapi.config.SecurityConfig;
import com.seydi.pharmacie.pharmacieapi.controller.ClientController;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ClientResponse;
import com.seydi.pharmacie.pharmacieapi.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(ClientController.class)
@EnableWebSecurity
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "jwt.secret=une-cle-secrete-de-test-assez-longue-pour-hs256"
})
class ClientSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private ClientDetailsService clientDetailsService;


    // =========================================================
    // POST /clients
    // =========================================================

    @Test
    void postClient_sansAuthentification_doitEtreAutorise()
            throws Exception {

        ClientResponse response =
                mock(ClientResponse.class);

        when(clientService.ajouterClient(
                any(CreateClientRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                post("/clients")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Seydi",
                                    "email": "seydi@test.com",
                                    "motDePasse": "password123",
                                    "telephone": "771234567",
                                    "adresse": "Dakar"
                                }
                                """)
        ).andExpect(status().isCreated());

        verify(clientService)
                .ajouterClient(any(CreateClientRequest.class));
    }


    // =========================================================
    // GET /clients
    // =========================================================

    @Test
    void getClients_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                get("/clients")
        ).andExpect(status().isUnauthorized());

        verify(clientService, never())
                .listerClients();
    }


    @Test
    void getClients_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                get("/clients")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isForbidden());

        verify(clientService, never())
                .listerClients();
    }


    @Test
    void getClients_avecRoleAdmin_doitRetourner200()
            throws Exception {

        when(clientService.listerClients())
                .thenReturn(java.util.List.of());

        mockMvc.perform(
                get("/clients")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isOk());

        verify(clientService)
                .listerClients();
    }


    // =========================================================
    // GET /clients/me
    // =========================================================

    @Test
    void getMonProfil_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                get("/clients/me")
        ).andExpect(status().isUnauthorized());

        verify(clientService, never())
                .chercherMonProfil(any());
    }


    @Test
    void getMonProfil_avecRoleClient_doitRetourner200()
            throws Exception {

        ClientResponse response =
                mock(ClientResponse.class);

        when(clientService.chercherMonProfil(any()))
                .thenReturn(response);

        mockMvc.perform(
                get("/clients/me")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isOk());

        verify(clientService)
                .chercherMonProfil(any());
    }


    @Test
    void getMonProfil_avecRoleAdmin_doitRetourner200()
            throws Exception {

        ClientResponse response =
                mock(ClientResponse.class);

        when(clientService.chercherMonProfil(any()))
                .thenReturn(response);

        mockMvc.perform(
                get("/clients/me")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isOk());

        verify(clientService)
                .chercherMonProfil(any());
    }


    // =========================================================
    // GET /clients/{id}
    // =========================================================

    @Test
    void getClientParId_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                get("/clients/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isForbidden());

        verify(clientService, never())
                .chercherClientParId(eq(1L), any());
    }


    @Test
    void getClientParId_avecRoleAdmin_doitRetourner200()
            throws Exception {

        ClientResponse response =
                mock(ClientResponse.class);

        when(clientService.chercherClientParId(
                eq(1L),
                any()
        )).thenReturn(response);

        mockMvc.perform(
                get("/clients/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isOk());

        verify(clientService)
                .chercherClientParId(eq(1L), any());
    }


    // =========================================================
    // PUT /clients/me
    // =========================================================

    @Test
    void putMonProfil_avecRoleClient_doitRetourner200()
            throws Exception {

        UpdateClientRequest request =
                new UpdateClientRequest(
                        "Seydi Modifie",
                        "seydi2@test.com",
                        "771234568",
                        "Dakar",
                        null
                );

        ClientResponse response =
                mock(ClientResponse.class);

        when(clientService.modifierMonProfil(
                any(),
                any(UpdateClientRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                put("/clients/me")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Seydi Modifie",
                                    "email": "seydi2@test.com",
                                    "telephone": "771234568",
                                    "adresse": "Dakar"
                                }
                                """)
        ).andExpect(status().isOk());

        verify(clientService)
                .modifierMonProfil(
                        any(),
                        any(UpdateClientRequest.class)
                );
    }


    @Test
    void putMonProfil_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                put("/clients/me")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Seydi Modifie",
                                    "email": "seydi2@test.com",
                                    "telephone": "771234568",
                                    "adresse": "Dakar"
                                }
                                """)
        ).andExpect(status().isUnauthorized());

        verify(clientService, never())
                .modifierMonProfil(
                        any(),
                        any(UpdateClientRequest.class)
                );
    }


    // =========================================================
    // PUT /clients/{id}
    // =========================================================

    @Test
    void putClientParId_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                put("/clients/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Seydi Modifie",
                                    "email": "seydi2@test.com",
                                    "telephone": "771234568",
                                    "adresse": "Dakar"
                                }
                                """)
        ).andExpect(status().isForbidden());

        verify(clientService, never())
                .modifierClient(
                        eq(1L),
                        any(),
                        any(UpdateClientRequest.class)
                );
    }


    @Test
    void putClientParId_avecRoleAdmin_doitRetourner200()
            throws Exception {

        UpdateClientRequest request =
                new UpdateClientRequest(
                        "Seydi Modifie",
                        "seydi2@test.com",
                        "771234568",
                        "Dakar",
                        null
                );

        ClientResponse response =
                mock(ClientResponse.class);

        when(clientService.modifierClient(
                eq(1L),
                any(),
                any(UpdateClientRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                put("/clients/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "nom": "Seydi Modifie",
                                    "email": "seydi2@test.com",
                                    "telephone": "771234568",
                                    "adresse": "Dakar"
                                }
                                """)
        ).andExpect(status().isOk());

        verify(clientService)
                .modifierClient(
                        eq(1L),
                        any(),
                        any(UpdateClientRequest.class)
                );
    }


    // =========================================================
    // DELETE /clients/me
    // =========================================================

    @Test
    void deleteMonProfil_avecRoleClient_doitRetourner204()
            throws Exception {

        doNothing()
                .when(clientService)
                .supprimerMonProfil(any());

        mockMvc.perform(
                delete("/clients/me")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isNoContent());

        verify(clientService)
                .supprimerMonProfil(any());
    }


    @Test
    void deleteMonProfil_avecRoleAdmin_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                delete("/clients/me")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isForbidden());

        verify(clientService, never())
                .supprimerMonProfil(any());
    }


    // =========================================================
    // DELETE /clients/{id}
    // =========================================================

    @Test
    void deleteClientParId_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                delete("/clients/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isForbidden());

        verify(clientService, never())
                .supprimerClient(eq(1L), any());
    }


    @Test
    void deleteClientParId_avecRoleAdmin_doitRetourner204()
            throws Exception {

        doNothing()
                .when(clientService)
                .supprimerClient(eq(1L), any());

        mockMvc.perform(
                delete("/clients/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isNoContent());

        verify(clientService)
                .supprimerClient(eq(1L), any());
    }
}