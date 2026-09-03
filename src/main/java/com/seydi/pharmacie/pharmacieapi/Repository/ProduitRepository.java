package com.seydi.pharmacie.pharmacieapi.repository;

import com.seydi.pharmacie.pharmacieapi.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    //Demander à Jpa de vérifier si le nom du produit existe
    boolean existsByNom(String nom);
}
