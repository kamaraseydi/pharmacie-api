package com.seydi.pharmacie.pharmacieapi.Repository;

import com.seydi.pharmacie.pharmacieapi.Model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
}
