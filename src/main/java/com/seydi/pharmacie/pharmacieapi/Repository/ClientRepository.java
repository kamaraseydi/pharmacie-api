package com.seydi.pharmacie.pharmacieapi.repository;

import com.seydi.pharmacie.pharmacieapi.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    //Demander à Jpa de vérifier si l'email existe
    boolean existsByEmail(String email);

    //Rechercher un client par son email
    Optional<Client> findByEmail(String email);
}
