package com.seydi.pharmacie.pharmacieapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.StockResponse;
import com.seydi.pharmacie.pharmacieapi.service.StockService;
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
class StockControllerTest {

    @Mock
    private StockService stockService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {

        StockController stockController =
                new StockController(stockService);

        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(stockController)
                .setValidator(new SpringValidatorAdapter(validator))
                .build();
    }


    // =========================================================
    // POST /stocks
    // =========================================================

    @Test
    void ajouterStock_donneesValides_doitRetourner201()
            throws Exception {

        CreateStockRequest request =
                new CreateStockRequest(1L, 50);

        StockResponse response =
                mock(StockResponse.class);

        when(stockService.ajouterStock(
                any(CreateStockRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/stocks")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        verify(stockService)
                .ajouterStock(any(CreateStockRequest.class));
    }


    @Test
    void ajouterStock_donneesInvalides_doitRetourner400()
            throws Exception {

        CreateStockRequest request =
                new CreateStockRequest();

        mockMvc.perform(
                        post("/stocks")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(stockService, never())
                .ajouterStock(any(CreateStockRequest.class));
    }


    // =========================================================
    // GET /stocks
    // =========================================================

    @Test
    void listerStocks_doitRetourner200()
            throws Exception {

        when(stockService.listerStocks())
                .thenReturn(java.util.List.of());

        mockMvc.perform(
                        get("/stocks")
                )
                .andExpect(status().isOk());

        verify(stockService)
                .listerStocks();
    }


    // =========================================================
    // GET /stocks/{id}
    // =========================================================

    @Test
    void chercherStockParId_doitRetourner200()
            throws Exception {

        Long id = 1L;

        StockResponse response =
                mock(StockResponse.class);

        when(stockService.chercherStockParId(id))
                .thenReturn(response);

        mockMvc.perform(
                        get("/stocks/{id}", id)
                )
                .andExpect(status().isOk());

        verify(stockService)
                .chercherStockParId(id);
    }


    // =========================================================
    // PUT /stocks/{id}
    // =========================================================

    @Test
    void modifierStock_donneesValides_doitRetourner200()
            throws Exception {

        Long id = 1L;

        UpdateStockRequest request =
                new UpdateStockRequest(100);

        StockResponse response =
                mock(StockResponse.class);

        when(stockService.modifierStock(
                eq(id),
                any(UpdateStockRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/stocks/{id}", id)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(stockService)
                .modifierStock(
                        eq(id),
                        any(UpdateStockRequest.class)
                );
    }


    @Test
    void modifierStock_donneesInvalides_doitRetourner400()
            throws Exception {

        Long id = 1L;

        UpdateStockRequest request =
                new UpdateStockRequest();

        mockMvc.perform(
                        put("/stocks/{id}", id)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(stockService, never())
                .modifierStock(
                        eq(id),
                        any(UpdateStockRequest.class)
                );
    }


    // =========================================================
    // DELETE /stocks/{id}
    // =========================================================

    @Test
    void supprimerStock_doitRetourner204()
            throws Exception {

        Long id = 1L;

        doNothing()
                .when(stockService)
                .supprimerStock(id);

        mockMvc.perform(
                        delete("/stocks/{id}", id)
                )
                .andExpect(status().isNoContent());

        verify(stockService)
                .supprimerStock(id);
    }
}