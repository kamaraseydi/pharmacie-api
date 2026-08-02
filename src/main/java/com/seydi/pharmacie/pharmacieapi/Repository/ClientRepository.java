package com.seydi.pharmacie.pharmacieapi.Repository;

import com.seydi.pharmacie.pharmacieapi.Model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    //Demander à Jpa de vérifier si l'email existe
    boolean existsByEmail(String email);
}
