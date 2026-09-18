package com.seydi.pharmacie.pharmacieapi.controller;

import com.seydi.pharmacie.pharmacieapi.dto.request.CreateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ClientResponse;
import com.seydi.pharmacie.pharmacieapi.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {

        ClientController clientController =
                new ClientController(clientService);

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders
                .standaloneSetup(clientController)
                .setValidator(validator)
                .build();
    }


    // =========================================================
    // GET /clients
    // =========================================================

    @Test
    void listerClients_doitRetourner200() throws Exception {

        when(clientService.listerClients())
                .thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/clients")
                )
                .andExpect(status().isOk());

        verify(clientService)
                .listerClients();
    }


    // =========================================================
    // POST /clients
    // =========================================================

    @Test
    void ajouterClient_donneesValides_doitRetourner201() throws Exception {

        ClientResponse response =
                mock(ClientResponse.class);

        CreateClientRequest request =
                new CreateClientRequest();

        request.setNom("Moussa");
        request.setEmail("moussa@example.com");
        request.setMotDePasse("Password123");
        request.setTelephone("771234567");
        request.setAdresse("Dakar");

        when(clientService.ajouterClient(any(CreateClientRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/clients")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        verify(clientService)
                .ajouterClient(any(CreateClientRequest.class));
    }


    @Test
    void ajouterClient_donneesInvalides_doitRetourner400() throws Exception {

        CreateClientRequest request =
                new CreateClientRequest();

        request.setNom("");
        request.setEmail("email-invalide");
        request.setMotDePasse("123");
        request.setTelephone("123");
        request.setAdresse("D");

        mockMvc.perform(
                        post("/clients")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(clientService, never())
                .ajouterClient(any(CreateClientRequest.class));
    }


    // =========================================================
    // GET /clients/me
    // =========================================================

    @Test
    void chercherMonProfil_doitRetourner200() throws Exception {

        ClientResponse response =
                mock(ClientResponse.class);

        when(clientService.chercherMonProfil(authentication))
                .thenReturn(response);

        mockMvc.perform(
                        get("/clients/me")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(clientService)
                .chercherMonProfil(authentication);
    }


    // =========================================================
    // GET /clients/{id}
    // =========================================================

    @Test
    void chercherClientParId_doitRetourner200() throws Exception {

        Long id = 15L;

        ClientResponse response =
                mock(ClientResponse.class);

        when(clientService.chercherClientParId(id, authentication))
                .thenReturn(response);

        mockMvc.perform(
                        get("/clients/{id}", id)
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(clientService)
                .chercherClientParId(id, authentication);
    }


    // =========================================================
    // PUT /clients/me
    // =========================================================

    @Test
    void modifierMonProfil_donneesValides_doitRetourner200()
            throws Exception {

        ClientResponse response =
                mock(ClientResponse.class);

        UpdateClientRequest request =
                new UpdateClientRequest();

        request.setNom("Moussa");
        request.setEmail("moussa@example.com");
        request.setTelephone("771234567");
        request.setAdresse("Dakar");

        when(clientService.modifierMonProfil(
                eq(authentication),
                any(UpdateClientRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/clients/me")
                                .principal(authentication)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(clientService)
                .modifierMonProfil(
                        eq(authentication),
                        any(UpdateClientRequest.class)
                );
    }


    @Test
    void modifierMonProfil_donneesInvalides_doitRetourner400()
            throws Exception {

        UpdateClientRequest request =
                new UpdateClientRequest();

        request.setNom("");
        request.setEmail("email-invalide");
        request.setTelephone("123");
        request.setAdresse("D");

        mockMvc.perform(
                        put("/clients/me")
                                .principal(authentication)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

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
    void modifierClient_donneesValides_doitRetourner200()
            throws Exception {

        Long id = 15L;

        ClientResponse response =
                mock(ClientResponse.class);

        UpdateClientRequest request =
                new UpdateClientRequest();

        request.setNom("Moussa");
        request.setEmail("moussa@example.com");
        request.setTelephone("771234567");
        request.setAdresse("Dakar");

        when(clientService.modifierClient(
                eq(id),
                eq(authentication),
                any(UpdateClientRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/clients/{id}", id)
                                .principal(authentication)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(clientService)
                .modifierClient(
                        eq(id),
                        eq(authentication),
                        any(UpdateClientRequest.class)
                );
    }


    // =========================================================
    // DELETE /clients/me
    // =========================================================

    @Test
    void supprimerMonProfil_doitRetourner204()
            throws Exception {

        doNothing()
                .when(clientService)
                .supprimerMonProfil(authentication);

        mockMvc.perform(
                        delete("/clients/me")
                                .principal(authentication)
                )
                .andExpect(status().isNoContent());

        verify(clientService)
                .supprimerMonProfil(authentication);
    }


    // =========================================================
    // DELETE /clients/{id}
    // =========================================================

    @Test
    void supprimerClient_doitRetourner204()
            throws Exception {

        Long id = 15L;

        doNothing()
                .when(clientService)
                .supprimerClient(id, authentication);

        mockMvc.perform(
                        delete("/clients/{id}", id)
                                .principal(authentication)
                )
                .andExpect(status().isNoContent());

        verify(clientService)
                .supprimerClient(id, authentication);
    }
}