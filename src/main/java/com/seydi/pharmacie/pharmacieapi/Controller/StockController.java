package com.seydi.pharmacie.pharmacieapi.Controller;

import com.seydi.pharmacie.pharmacieapi.Service.StockService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.StockResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;


    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    //Récupérer les produits avec leurs stock
    @GetMapping
    public List<StockResponse> listerStocks(){
        return stockService.listerStocks();
    }

    //Ajout de stock
    @PostMapping
    public StockResponse ajouterStock(@Valid @RequestBody CreateStockRequest request){
        return stockService.ajouterStock(request);
    }

    //Chercher stock
    @GetMapping("/{id}")
    public StockResponse chercherStockParId(@PathVariable Long id){
        return stockService.chercherStockParId(id);
    }

    //Modifier stock
    @PutMapping("/{id}")
    public StockResponse modifierStock(@PathVariable Long id, @Valid @RequestBody UpdateStockRequest request){
        return stockService.modifierStock(id,request);
    }

    //supprimer stock
    @DeleteMapping("/{id}")
    public void supprimerStock(@PathVariable Long id){
        stockService.supprimerStock(id);
    }
}
