package com.seydi.pharmacie.pharmacieapi.security;

import com.seydi.pharmacie.pharmacieapi.config.SecurityConfig;
import com.seydi.pharmacie.pharmacieapi.controller.StockController;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.StockResponse;
import com.seydi.pharmacie.pharmacieapi.service.StockService;

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


@WebMvcTest(StockController.class)
@EnableWebSecurity
@Import({
        SecurityConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "jwt.secret=une-cle-secrete-de-test-assez-longue-pour-hs256"
})
class StockSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService stockService;

    @MockitoBean
    private ClientDetailsService clientDetailsService;


    // =========================================================
    // POST /stocks
    // =========================================================

    @Test
    void postStock_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                post("/stocks")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "produitId": 1,
                                    "quantite": 50
                                }
                                """)
        ).andExpect(status().isUnauthorized());

        verify(stockService, never())
                .ajouterStock(any(CreateStockRequest.class));
    }


    @Test
    void postStock_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                post("/stocks")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "produitId": 1,
                                    "quantite": 50
                                }
                                """)
        ).andExpect(status().isForbidden());

        verify(stockService, never())
                .ajouterStock(any(CreateStockRequest.class));
    }


    @Test
    void postStock_avecRoleAdmin_doitRetourner201()
            throws Exception {

        StockResponse response =
                mock(StockResponse.class);

        when(stockService.ajouterStock(
                any(CreateStockRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                post("/stocks")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "produitId": 1,
                                    "quantite": 50
                                }
                                """)
        ).andExpect(status().isCreated());

        verify(stockService)
                .ajouterStock(any(CreateStockRequest.class));
    }


    // =========================================================
    // GET /stocks
    // =========================================================

    @Test
    void getStocks_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                get("/stocks")
        ).andExpect(status().isUnauthorized());

        verify(stockService, never())
                .listerStocks();
    }


    @Test
    void getStocks_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                get("/stocks")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isForbidden());

        verify(stockService, never())
                .listerStocks();
    }


    @Test
    void getStocks_avecRoleAdmin_doitRetourner200()
            throws Exception {

        when(stockService.listerStocks())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/stocks")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isOk());

        verify(stockService)
                .listerStocks();
    }


    // =========================================================
    // GET /stocks/{id}
    // =========================================================

    @Test
    void getStockParId_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                get("/stocks/1")
        ).andExpect(status().isUnauthorized());

        verify(stockService, never())
                .chercherStockParId(any());
    }


    @Test
    void getStockParId_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                get("/stocks/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isForbidden());

        verify(stockService, never())
                .chercherStockParId(any());
    }


    @Test
    void getStockParId_avecRoleAdmin_doitRetourner200()
            throws Exception {

        StockResponse response =
                mock(StockResponse.class);

        when(stockService.chercherStockParId(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/stocks/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isOk());

        verify(stockService)
                .chercherStockParId(eq(1L));
    }


    // =========================================================
    // PUT /stocks/{id}
    // =========================================================

    @Test
    void putStock_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                put("/stocks/1")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "quantite": 100
                                }
                                """)
        ).andExpect(status().isUnauthorized());

        verify(stockService, never())
                .modifierStock(
                        any(),
                        any(UpdateStockRequest.class)
                );
    }


    @Test
    void putStock_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                put("/stocks/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "quantite": 100
                                }
                                """)
        ).andExpect(status().isForbidden());

        verify(stockService, never())
                .modifierStock(
                        any(),
                        any(UpdateStockRequest.class)
                );
    }


    @Test
    void putStock_avecRoleAdmin_doitRetourner200()
            throws Exception {

        StockResponse response =
                mock(StockResponse.class);

        when(stockService.modifierStock(
                eq(1L),
                any(UpdateStockRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                put("/stocks/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "quantite": 100
                                }
                                """)
        ).andExpect(status().isOk());

        verify(stockService)
                .modifierStock(
                        eq(1L),
                        any(UpdateStockRequest.class)
                );
    }


    // =========================================================
    // DELETE /stocks/{id}
    // =========================================================

    @Test
    void deleteStock_sansAuthentification_doitRetourner401()
            throws Exception {

        mockMvc.perform(
                delete("/stocks/1")
        ).andExpect(status().isUnauthorized());

        verify(stockService, never())
                .supprimerStock(any());
    }


    @Test
    void deleteStock_avecRoleClient_doitRetourner403()
            throws Exception {

        mockMvc.perform(
                delete("/stocks/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CLIENT")
                        ))
        ).andExpect(status().isForbidden());

        verify(stockService, never())
                .supprimerStock(any());
    }


    @Test
    void deleteStock_avecRoleAdmin_doitRetourner204()
            throws Exception {

        doNothing()
                .when(stockService)
                .supprimerStock(eq(1L));

        mockMvc.perform(
                delete("/stocks/1")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        ))
        ).andExpect(status().isNoContent());

        verify(stockService)
                .supprimerStock(eq(1L));
    }
}