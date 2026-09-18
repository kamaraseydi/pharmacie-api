package com.seydi.pharmacie.pharmacieapi.security;

import com.seydi.pharmacie.pharmacieapi.controller.AuthController;
import com.seydi.pharmacie.pharmacieapi.dto.request.LoginRequest;
import com.seydi.pharmacie.pharmacieapi.security.AuthenticationService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;


    // =========================================================
    // POST /auth
    // =========================================================

    @Test
    void authentifierClient_avecIdentifiantsValides_doitRetourner200()
            throws Exception {

        when(authenticationService.login(
                any(LoginRequest.class)
        )).thenReturn("jwt-token-test");

        mockMvc.perform(
                        post("/auth")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                {
                                    "email": "seydi@test.com",
                                    "motDePasse": "password123"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token-test"));

        verify(authenticationService)
                .login(any(LoginRequest.class));
    }


    @Test
    void authentifierClient_avecEmailInvalide_doitRetourner400()
            throws Exception {

        mockMvc.perform(
                        post("/auth")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                {
                                    "email": "email-invalide",
                                    "motDePasse": "password123"
                                }
                                """)
                )
                .andExpect(status().isBadRequest());

        verify(authenticationService, never())
                .login(any(LoginRequest.class));
    }


    @Test
    void authentifierClient_avecEmailVide_doitRetourner400()
            throws Exception {

        mockMvc.perform(
                        post("/auth")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                {
                                    "email": "",
                                    "motDePasse": "password123"
                                }
                                """)
                )
                .andExpect(status().isBadRequest());

        verify(authenticationService, never())
                .login(any(LoginRequest.class));
    }


    @Test
    void authentifierClient_avecMotDePasseTropCourt_doitRetourner400()
            throws Exception {

        mockMvc.perform(
                        post("/auth")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                {
                                    "email": "seydi@test.com",
                                    "motDePasse": "1234567"
                                }
                                """)
                )
                .andExpect(status().isBadRequest());

        verify(authenticationService, never())
                .login(any(LoginRequest.class));
    }


    @Test
    void authentifierClient_avecMotDePasseVide_doitRetourner400()
            throws Exception {

        mockMvc.perform(
                        post("/auth")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                {
                                    "email": "seydi@test.com",
                                    "motDePasse": ""
                                }
                                """)
                )
                .andExpect(status().isBadRequest());

        verify(authenticationService, never())
                .login(any(LoginRequest.class));
    }
}