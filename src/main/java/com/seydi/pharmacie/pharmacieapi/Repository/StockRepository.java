package com.seydi.pharmacie.pharmacieapi.repository;

import com.seydi.pharmacie.pharmacieapi.model.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    //Methode pour vérifier si ce produit possede déja un stock car dans
    //notre appli pour l'instant un produit n'a qu'un seul stock

    boolean existsByProduitId(Long id);

    //demander à la base de verrouiller la ligne concernée pendant la transaction.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Stock> findByProduitId(Long produitId); //trouver le stock du produit
}
