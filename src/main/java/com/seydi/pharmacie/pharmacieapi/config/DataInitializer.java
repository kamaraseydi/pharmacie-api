package com.seydi.pharmacie.pharmacieapi.config;

import com.seydi.pharmacie.pharmacieapi.model.Client;
import com.seydi.pharmacie.pharmacieapi.model.Role;
import com.seydi.pharmacie.pharmacieapi.repository.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(ClientRepository clientRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (clientRepository.findByEmail("admin@pharmacie.com").isEmpty()) {

                Client admin = new Client();

                admin.setNom("Administrateur");
                admin.setEmail("admin@pharmacie.com");
                admin.setMotDePasse(
                        passwordEncoder.encode("Admin123456")
                );
                admin.setTelephone("771234567");
                admin.setAdresse("Dakar");
                admin.setRole(Role.ADMIN);

                clientRepository.save(admin);

                System.out.println("Compte ADMIN créé.");
            }
        };
    }
}
