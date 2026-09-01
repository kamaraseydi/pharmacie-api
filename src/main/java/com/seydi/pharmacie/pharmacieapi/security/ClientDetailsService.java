package com.seydi.pharmacie.pharmacieapi.security;

import com.seydi.pharmacie.pharmacieapi.Model.Client;
import com.seydi.pharmacie.pharmacieapi.Repository.ClientRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ClientDetailsService implements UserDetailsService {

    //UserDetailsService Il sait comment retrouver l'utilisateur pour Spring Security.

    private final ClientRepository clientRepository;


    public ClientDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Vérifier si le client existe
       Client clientExistant = clientRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("Client introuvable"));

        return User.withUsername(clientExistant.getId().toString())
                .password(clientExistant.getMotDePasse())
                .roles(clientExistant.getRole().name())
                .build();
    }
}
