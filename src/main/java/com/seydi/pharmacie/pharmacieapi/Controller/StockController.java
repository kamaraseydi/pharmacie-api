package com.seydi.pharmacie.pharmacieapi.Controller;

import com.seydi.pharmacie.pharmacieapi.Service.StockService;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateStockRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.StockResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<StockResponse> ajouterStock(@Valid @RequestBody CreateStockRequest request){
        StockResponse stockResponse = stockService.ajouterStock(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockResponse);
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
    public ResponseEntity<Void> supprimerStock(@PathVariable Long id){

        stockService.supprimerStock(id);

        return ResponseEntity.noContent().build();
    }
}
