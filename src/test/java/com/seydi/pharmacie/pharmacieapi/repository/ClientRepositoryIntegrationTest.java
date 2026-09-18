package com.seydi.pharmacie.pharmacieapi.repository;

import com.seydi.pharmacie.pharmacieapi.model.Client;
import com.seydi.pharmacie.pharmacieapi.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@Transactional
class ClientRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.4")
                    .withStartupTimeout(Duration.ofMinutes(5));

    @Autowired
    private ClientRepository clientRepository;


    @Test
    void sauvegarderClient_doitEtreRetrouveParSonId() {

        Client client = new Client();

        client.setNom("Seydi");
        client.setEmail("seydi@test.com");
        client.setMotDePasse("password123");
        client.setTelephone("771234567");
        client.setAdresse("Dakar");
        client.setRole(Role.CLIENT);

        Client clientSauvegarde =
                clientRepository.saveAndFlush(client);

        Client clientTrouve =
                clientRepository.findById(clientSauvegarde.getId())
                        .orElseThrow();

        assertThat(clientTrouve.getId())
                .isNotNull();

        assertThat(clientTrouve.getNom())
                .isEqualTo("Seydi");

        assertThat(clientTrouve.getEmail())
                .isEqualTo("seydi@test.com");
    }


    @Test
    void existsByEmail_emailExistant_doitRetournerTrue() {

        Client client = new Client();

        client.setNom("Seydi");
        client.setEmail("exists@test.com");
        client.setMotDePasse("password123");
        client.setTelephone("771234567");
        client.setAdresse("Dakar");
        client.setRole(Role.CLIENT);

        clientRepository.saveAndFlush(client);

        boolean existe =
                clientRepository.existsByEmail("exists@test.com");

        assertThat(existe)
                .isTrue();
    }


    @Test
    void existsByEmail_emailInexistant_doitRetournerFalse() {

        boolean existe =
                clientRepository.existsByEmail("inexistant@test.com");

        assertThat(existe)
                .isFalse();
    }


    @Test
    void findByEmail_emailExistant_doitRetournerClient() {

        Client client = new Client();

        client.setNom("Seydi");
        client.setEmail("find@test.com");
        client.setMotDePasse("password123");
        client.setTelephone("771234567");
        client.setAdresse("Dakar");
        client.setRole(Role.CLIENT);

        clientRepository.saveAndFlush(client);

        Client clientTrouve =
                clientRepository.findByEmail("find@test.com")
                        .orElseThrow();

        assertThat(clientTrouve.getNom())
                .isEqualTo("Seydi");

        assertThat(clientTrouve.getEmail())
                .isEqualTo("find@test.com");
    }


    @Test
    void findByEmail_emailInexistant_doitRetournerVide() {

        assertThat(
                clientRepository.findByEmail("inexistant@test.com")
        ).isEmpty();
    }
}