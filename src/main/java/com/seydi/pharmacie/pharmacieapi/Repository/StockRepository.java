package com.seydi.pharmacie.pharmacieapi.Repository;

import com.seydi.pharmacie.pharmacieapi.Model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
    //Methode pour vérifier si ce produit possede déja un stock car dans
    //notre appli pour l'instant un produit n'a qu'un seul stock

    boolean existsByProduitId(Long id);
}
