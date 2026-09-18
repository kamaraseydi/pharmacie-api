package com.seydi.pharmacie.pharmacieapi.service;

import com.seydi.pharmacie.pharmacieapi.dto.request.CreateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.StockResponse;
import com.seydi.pharmacie.pharmacieapi.exception.ProduitNotFoundException;
import com.seydi.pharmacie.pharmacieapi.exception.StockAlreadyExistsException;
import com.seydi.pharmacie.pharmacieapi.exception.StockNotFoundException;
import com.seydi.pharmacie.pharmacieapi.mapper.StockMapper;
import com.seydi.pharmacie.pharmacieapi.model.Produit;
import com.seydi.pharmacie.pharmacieapi.model.Stock;
import com.seydi.pharmacie.pharmacieapi.repository.ProduitRepository;
import com.seydi.pharmacie.pharmacieapi.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private StockMapper stockMapper;

    @InjectMocks
    private StockService stockService;


    // =========================================================
    // AJOUTER STOCK
    // =========================================================

    @Test
    void ajouterStock_doitAjouterAvecSucces() {

        CreateStockRequest request = mock(CreateStockRequest.class);

        Produit produit = mock(Produit.class);
        Stock stock = mock(Stock.class);
        Stock stockSauvegarde = mock(Stock.class);
        StockResponse response = mock(StockResponse.class);

        when(request.getProduitId())
                .thenReturn(1L);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        when(stockRepository.existsByProduitId(1L))
                .thenReturn(false);

        when(stockMapper.toEntity(request))
                .thenReturn(stock);

        when(stockRepository.save(stock))
                .thenReturn(stockSauvegarde);

        when(stockMapper.toResponse(stockSauvegarde))
                .thenReturn(response);

        StockResponse result =
                stockService.ajouterStock(request);

        assertEquals(response, result);

        verify(produitRepository)
                .findById(1L);

        verify(stockRepository)
                .existsByProduitId(1L);

        verify(stockMapper)
                .toEntity(request);

        verify(stock).setProduit(produit);

        verify(produit).setStock(stock);

        verify(stockRepository)
                .save(stock);

        verify(stockMapper)
                .toResponse(stockSauvegarde);
    }


    @Test
    void ajouterStock_avecProduitInexistant_doitLeverException() {

        CreateStockRequest request =
                mock(CreateStockRequest.class);

        when(request.getProduitId())
                .thenReturn(99L);

        when(produitRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProduitNotFoundException.class,
                () -> stockService.ajouterStock(request)
        );

        verify(produitRepository)
                .findById(99L);

        verify(stockRepository, never())
                .existsByProduitId(any());

        verify(stockRepository, never())
                .save(any());

        verifyNoInteractions(stockMapper);
    }


    @Test
    void ajouterStock_avecStockDejaExistant_doitLeverException() {

        CreateStockRequest request =
                mock(CreateStockRequest.class);

        Produit produit =
                mock(Produit.class);

        when(request.getProduitId())
                .thenReturn(1L);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        when(stockRepository.existsByProduitId(1L))
                .thenReturn(true);

        assertThrows(
                StockAlreadyExistsException.class,
                () -> stockService.ajouterStock(request)
        );

        verify(produitRepository)
                .findById(1L);

        verify(stockRepository)
                .existsByProduitId(1L);

        verify(stockRepository, never())
                .save(any());

        verifyNoInteractions(stockMapper);

        verify(produit, never())
                .setStock(any());
    }


    // =========================================================
    // LISTER STOCKS
    // =========================================================

    @Test
    void listerStocks_doitRetournerLaListe() {

        Stock stock1 = mock(Stock.class);
        Stock stock2 = mock(Stock.class);

        StockResponse response1 = mock(StockResponse.class);
        StockResponse response2 = mock(StockResponse.class);

        when(stockRepository.findAll())
                .thenReturn(List.of(stock1, stock2));

        when(stockMapper.toResponse(stock1))
                .thenReturn(response1);

        when(stockMapper.toResponse(stock2))
                .thenReturn(response2);

        List<StockResponse> result =
                stockService.listerStocks();

        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(stockRepository)
                .findAll();

        verify(stockMapper)
                .toResponse(stock1);

        verify(stockMapper)
                .toResponse(stock2);
    }


    @Test
    void listerStocks_sansStock_doitRetournerListeVide() {

        when(stockRepository.findAll())
                .thenReturn(List.of());

        List<StockResponse> result =
                stockService.listerStocks();

        assertTrue(result.isEmpty());

        verify(stockRepository)
                .findAll();

        verifyNoInteractions(stockMapper);
    }


    // =========================================================
    // CHERCHER STOCK
    // =========================================================

    @Test
    void chercherStockParId_doitRetournerLeStock() {

        Long id = 1L;

        Stock stock =
                mock(Stock.class);

        StockResponse response =
                mock(StockResponse.class);

        when(stockRepository.findById(id))
                .thenReturn(Optional.of(stock));

        when(stockMapper.toResponse(stock))
                .thenReturn(response);

        StockResponse result =
                stockService.chercherStockParId(id);

        assertEquals(response, result);

        verify(stockRepository)
                .findById(id);

        verify(stockMapper)
                .toResponse(stock);
    }


    @Test
    void chercherStockParId_stockInexistant_doitLeverException() {

        Long id = 99L;

        when(stockRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                StockNotFoundException.class,
                () -> stockService.chercherStockParId(id)
        );

        verify(stockRepository)
                .findById(id);

        verifyNoInteractions(stockMapper);
    }


    // =========================================================
    // MODIFIER STOCK
    // =========================================================

    @Test
    void modifierStock_stockInexistant_doitLeverException() {

        Long id = 1L;

        UpdateStockRequest request =
                mock(UpdateStockRequest.class);

        when(stockRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                StockNotFoundException.class,
                () -> stockService.modifierStock(id, request)
        );

        verify(stockRepository)
                .findById(id);

        verifyNoInteractions(stockMapper);

        verify(stockRepository, never())
                .save(any());
    }


    @Test
    void modifierStock_doitModifierAvecSucces() {

        Long id = 1L;

        Stock stock =
                mock(Stock.class);

        UpdateStockRequest request =
                mock(UpdateStockRequest.class);

        Stock stockMisAJour =
                mock(Stock.class);

        StockResponse response =
                mock(StockResponse.class);

        when(stockRepository.findById(id))
                .thenReturn(Optional.of(stock));

        when(stockRepository.save(stock))
                .thenReturn(stockMisAJour);

        when(stockMapper.toResponse(stockMisAJour))
                .thenReturn(response);

        StockResponse result =
                stockService.modifierStock(id, request);

        assertEquals(response, result);

        verify(stockRepository)
                .findById(id);

        verify(stockMapper)
                .updateEntity(stock, request);

        verify(stockRepository)
                .save(stock);

        verify(stockMapper)
                .toResponse(stockMisAJour);
    }


    // =========================================================
    // SUPPRIMER STOCK
    // =========================================================

    @Test
    void supprimerStock_stockInexistant_doitLeverException() {

        Long id = 99L;

        when(stockRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                StockNotFoundException.class,
                () -> stockService.supprimerStock(id)
        );

        verify(stockRepository)
                .findById(id);

        verify(stockRepository, never())
                .delete(any());
    }


    @Test
    void supprimerStock_doitRompreRelationsEtSupprimer() {

        Long id = 1L;

        Stock stock =
                mock(Stock.class);

        Produit produit =
                mock(Produit.class);

        when(stockRepository.findById(id))
                .thenReturn(Optional.of(stock));

        when(stock.getProduit())
                .thenReturn(produit);

        stockService.supprimerStock(id);

        verify(stockRepository)
                .findById(id);

        verify(stock)
                .setProduit(null);

        verify(produit)
                .setStock(null);

        verify(stockRepository)
                .delete(stock);
    }
}