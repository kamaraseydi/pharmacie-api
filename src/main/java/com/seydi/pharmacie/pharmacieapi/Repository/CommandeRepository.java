package com.seydi.pharmacie.pharmacieapi.Repository;

import com.seydi.pharmacie.pharmacieapi.Model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    //Récupérer les commandes d'1 client
    List<Commande> findByClientId(Long id);
}
