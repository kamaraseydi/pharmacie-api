package com.seydi.pharmacie.pharmacieapi.security;

import com.seydi.pharmacie.pharmacieapi.config.SecurityConfig;
import com.seydi.pharmacie.pharmacieapi.controller.ProduitController;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateProduitRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ProduitResponse;
import com.seydi.pharmacie.pharmacieapi.service.ProduitService;
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
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProduitController.class)
@EnableWebSecurity
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "jwt.secret=une-cle-secrete-de-test-assez-longue-pour-hs256"
})
class ProduitSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProduitService produitService;

    @MockitoBean
    private ClientDetailsService clientDetailsService;


    // =========================================================
    // GET /produits
    // =========================================================

    @Test
    void getProduits_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                        get("/produits")
                )
                .andExpect(status().isUnauthorized());

        verify(produitService, never())
                .listerProduits();
    }


    @Test
    void getProduits_avecRoleClient_doitRetourner200()
            throws Exception {

        when(produitService.listerProduits())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/produits")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENT")
                                ))
                )
                .andExpect(status().isOk());

        verify(produitService)
                .listerProduits();
    }


    // =========================================================
    // POST /produits
    // =========================================================

    @Test
    void postProduit_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                        post("/produits")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENT")
                                ))
                                .contentType(APPLICATION_JSON)
                                .content("""
                                    {
                                        "nom": "Paracetamol",
                                        "description": "Antalgique pour douleurs",
                                        "prix": 1500.00,
                                        "fournisseurId": 1
                                    }
                                    """)
                )
                .andExpect(status().isForbidden());

        verify(produitService, never())
                .ajouterProduit(
                        any(CreateProduitRequest.class)
                );
    }


    @Test
    void postProduit_avecRoleAdmin_doitRetourner201()
            throws Exception {

        ProduitResponse response =
                mock(ProduitResponse.class);

        when(produitService.ajouterProduit(
                any(CreateProduitRequest.class)
        ))
                .thenReturn(response);

        mockMvc.perform(
                        post("/produits")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                                .contentType(APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "nom": "Paracetamol",
                                            "description": "Antalgique pour douleurs",
                                            "prix": 1500.00,
                                            "fournisseurId": 1
                                        }
                                        """
                                )
                )
                .andExpect(status().isCreated());

        verify(produitService)
                .ajouterProduit(
                        any(CreateProduitRequest.class)
                );
    }


    @Test
    void postProduit_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                        post("/produits")
                                .contentType(APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                            "nom": "Paracetamol",
                                            "description": "Antalgique pour douleurs",
                                            "prix": 1500.00,
                                            "fournisseurId": 1
                                        }
                                        """
                                )
                )
                .andExpect(status().isUnauthorized());

        verify(produitService, never())
                .ajouterProduit(
                        any(CreateProduitRequest.class)
                );
    }

}