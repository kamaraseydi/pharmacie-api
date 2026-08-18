package com.seydi.pharmacie.pharmacieapi.Repository;

import com.seydi.pharmacie.pharmacieapi.Model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    //Vérifier si le nom du fournisseur éxiste déja
    boolean existsByNom(String nom);
}
