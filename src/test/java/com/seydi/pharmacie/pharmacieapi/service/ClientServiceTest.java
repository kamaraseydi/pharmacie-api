package com.seydi.pharmacie.pharmacieapi.service;

import com.seydi.pharmacie.pharmacieapi.dto.request.CreateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ClientResponse;
import com.seydi.pharmacie.pharmacieapi.exception.ClientHasCommandesException;
import com.seydi.pharmacie.pharmacieapi.exception.ClientNotFoundException;
import com.seydi.pharmacie.pharmacieapi.exception.EmailAlreadyExistsException;
import com.seydi.pharmacie.pharmacieapi.mapper.ClientMapper;
import com.seydi.pharmacie.pharmacieapi.model.Client;
import com.seydi.pharmacie.pharmacieapi.model.Commande;
import com.seydi.pharmacie.pharmacieapi.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientService clientService;

    @Mock
    private Authentication authentication;

    @Test
    void ajouterClient_avecEmailDisponible_creeClient() {

        CreateClientRequest request = new CreateClientRequest(
                "Seydina",
                "seydina@test.com",
                "Test1234!",
                "771234567",
                "Dakar",
                LocalDate.of(2000, 5, 15)
        );

        when(clientRepository.existsByEmail("seydina@test.com"))
                .thenReturn(false);

        Client client = new Client();

        client.setNom("Seydina");
        client.setEmail("seydina@test.com");
        client.setMotDePasse("Test1234!");
        client.setTelephone("771234567");
        client.setAdresse("Dakar");
        client.setDateNaissance(LocalDate.of(2000, 5, 15));

        when(clientMapper.toEntity(request))
                .thenReturn(client);

        when(passwordEncoder.encode("Test1234!"))
                .thenReturn("HASHED_PASSWORD");

        when(clientRepository.save(client))
                .thenReturn(client);

        ClientResponse response = new ClientResponse(
                1L,
                "Seydina",
                "seydina@test.com",
                "771234567",
                "Dakar",
                LocalDate.of(2000, 5, 15)
        );

        when(clientMapper.toResponse(client))
                .thenReturn(response);

        // Exécution réelle du service
        ClientResponse result = clientService.ajouterClient(request);

        // Vérifications
        assertEquals("Seydina", result.getNom());
        assertEquals("seydina@test.com", result.getEmail());
        assertEquals("771234567", result.getTelephone());
        assertEquals("Dakar", result.getAdresse());
        assertEquals(LocalDate.of(2000, 5, 15), result.getDateNaissance());

        verify(passwordEncoder).encode("Test1234!");
        verify(clientRepository).save(client);

        assertEquals("HASHED_PASSWORD", client.getMotDePasse());
    }

    @Test
    void ajouterClient_avecEmailDejaUtilise_leveException() {

        CreateClientRequest request = new CreateClientRequest(
                "Seydina",
                "seydina@test.com",
                "Test1234!",
                "771234567",
                "Dakar",
                LocalDate.of(2000, 5, 15)
        );

        Client client = new Client();
        client.setEmail("seydina@test.com");

        when(clientMapper.toEntity(request))
                .thenReturn(client);

        when(clientRepository.existsByEmail("seydina@test.com"))
                .thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> clientService.ajouterClient(request)
        );

        assertEquals("Email déja utilisé", exception.getMessage());

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void chercherClientParId_clientConnecte_consulteSonProfil() {

        Client client = new Client();
        client.setId(1L);
        client.setNom("Seydina");
        client.setEmail("seydina@test.com");

        when(clientRepository.findById(1L))
                .thenReturn(java.util.Optional.of(client));

        when(authentication.getName())
                .thenReturn("1");

        ClientResponse response = new ClientResponse(
                1L,
                "Seydina",
                "seydina@test.com",
                "771234567",
                "Dakar",
                LocalDate.of(2000, 5, 15)
        );

        when(clientMapper.toResponse(client))
                .thenReturn(response);

        ClientResponse result =
                clientService.chercherClientParId(1L, authentication);

        assertEquals(1L, result.getId());
        assertEquals("Seydina", result.getNom());
        assertEquals("seydina@test.com", result.getEmail());

        verify(clientRepository).findById(1L);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void chercherClientParId_clientEssaieAccederAuProfilDUnAutre_leveException() {
        Client client = new Client();
        client.setId(2L);
        client.setNom("Autre Client");
        client.setEmail("autre@test.com");

        when(clientRepository.findById(2L))
                .thenReturn(java.util.Optional.of(client));

        when(authentication.getName())
                .thenReturn("1");

        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> clientService.chercherClientParId(2L, authentication)
        );

        verify(clientRepository).findById(2L);
        verify(clientMapper, never()).toResponse(any(Client.class));
    }

    @Test
    void chercherClientParId_clientInexistant_leveException() {

        when(clientRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> clientService.chercherClientParId(999L, authentication)
        );

        assertEquals("Client introuvable", exception.getMessage());

        verify(clientRepository).findById(999L);
        verify(clientMapper, never()).toResponse(any(Client.class));
    }

    @Test
    void chercherMonProfil_clientConnecte_retourneSonProfil() {

        Client client = new Client();
        client.setId(1L);
        client.setNom("Seydina");
        client.setEmail("seydina@test.com");

        when(authentication.getName())
                .thenReturn("1");

        when(clientRepository.findById(1L))
                .thenReturn(java.util.Optional.of(client));

        ClientResponse response = new ClientResponse(
                1L,
                "Seydina",
                "seydina@test.com",
                "771234567",
                "Dakar",
                LocalDate.of(2000, 5, 15)
        );

        when(clientMapper.toResponse(client))
                .thenReturn(response);

        ClientResponse result =
                clientService.chercherMonProfil(authentication);

        assertEquals(1L, result.getId());
        assertEquals("Seydina", result.getNom());
        assertEquals("seydina@test.com", result.getEmail());

        verify(authentication).getName();
        verify(clientRepository).findById(1L);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void modifierMonProfil_modificationValide_retourneProfilModifie() {

        Client client = new Client();
        client.setId(1L);
        client.setNom("Seydina");
        client.setEmail("seydina@test.com");
        client.setTelephone("771234567");
        client.setAdresse("Dakar");
        client.setDateNaissance(LocalDate.of(2000, 5, 15));

        when(authentication.getName())
                .thenReturn("1");

        when(clientRepository.findById(1L))
                .thenReturn(java.util.Optional.of(client));

        UpdateClientRequest request = new UpdateClientRequest(
                "Seydina Kamara",
                "seydina@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        doNothing().when(clientMapper)
                .updateEntity(client, request);

        when(clientRepository.save(client))
                .thenReturn(client);

        ClientResponse response = new ClientResponse(
                1L,
                "Seydina Kamara",
                "seydina@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        when(clientMapper.toResponse(client))
                .thenReturn(response);

        ClientResponse result =
                clientService.modifierMonProfil(authentication, request);

        assertEquals(1L, result.getId());
        assertEquals("Seydina Kamara", result.getNom());
        assertEquals("seydina@test.com", result.getEmail());
        assertEquals("776543210", result.getTelephone());
        assertEquals("Pikine", result.getAdresse());

        verify(authentication).getName();
        verify(clientRepository).findById(1L);
        verify(clientMapper).updateEntity(client, request);
        verify(clientRepository).save(client);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void modifierMonProfil_avecEmailDejaUtilise_leveException() {

        Client client = new Client();
        client.setId(1L);
        client.setNom("Seydina");
        client.setEmail("seydina@test.com");

        when(authentication.getName())
                .thenReturn("1");

        when(clientRepository.findById(1L))
                .thenReturn(java.util.Optional.of(client));

        UpdateClientRequest request = new UpdateClientRequest(
                "Seydina Kamara",
                "autre@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        when(clientRepository.existsByEmail("autre@test.com"))
                .thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> clientService.modifierMonProfil(authentication, request)
        );

        assertEquals("Email déja utilisé", exception.getMessage());

        verify(clientRepository).findById(1L);
        verify(clientRepository).existsByEmail("autre@test.com");
        verify(clientRepository, never()).save(any(Client.class));
        verify(clientMapper, never()).updateEntity(any(Client.class), any(UpdateClientRequest.class));
    }

    @Test
    void modifierMonProfil_avecMemeEmail_modifieSansVerifierDisponibilite() {

        Client client = new Client();
        client.setId(1L);
        client.setNom("Seydina");
        client.setEmail("seydina@test.com");
        client.setTelephone("771234567");
        client.setAdresse("Dakar");
        client.setDateNaissance(LocalDate.of(2000, 5, 15));

        when(authentication.getName())
                .thenReturn("1");

        when(clientRepository.findById(1L))
                .thenReturn(java.util.Optional.of(client));

        UpdateClientRequest request = new UpdateClientRequest(
                "Seydina Kamara",
                "seydina@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        ClientResponse response = new ClientResponse(
                1L,
                "Seydina Kamara",
                "seydina@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        when(clientRepository.save(client))
                .thenReturn(client);

        when(clientMapper.toResponse(client))
                .thenReturn(response);

        ClientResponse result =
                clientService.modifierMonProfil(authentication, request);

        assertEquals("Seydina Kamara", result.getNom());
        assertEquals("seydina@test.com", result.getEmail());
        assertEquals("776543210", result.getTelephone());
        assertEquals("Pikine", result.getAdresse());

        verify(clientRepository).findById(1L);
        verify(clientMapper).updateEntity(client, request);
        verify(clientRepository).save(client);
        verify(clientMapper).toResponse(client);

        verify(clientRepository, never())
                .existsByEmail(anyString());
    }

    @Test
    void supprimerMonProfil_sansCommande_supprimeClient() {

        Client client = new Client();
        client.setId(1L);
        client.setNom("Seydina");
        client.setEmail("seydina@test.com");

        when(authentication.getName())
                .thenReturn("1");

        when(clientRepository.findById(1L))
                .thenReturn(java.util.Optional.of(client));

        clientService.supprimerMonProfil(authentication);

        verify(authentication).getName();
        verify(clientRepository).findById(1L);
        verify(clientRepository).delete(client);
    }

    @Test
    void supprimerMonProfil_avecCommandes_leveException() {

        Client client = new Client();
        client.setId(1L);
        client.setNom("Seydina");
        client.setEmail("seydina@test.com");

        Commande commande = new Commande();
        client.getCommandes().add(commande);

        when(authentication.getName())
                .thenReturn("1");

        when(clientRepository.findById(1L))
                .thenReturn(java.util.Optional.of(client));

        ClientHasCommandesException exception = assertThrows(
                ClientHasCommandesException.class,
                () -> clientService.supprimerMonProfil(authentication)
        );

        assertEquals(
                "Impossible de supprimer le client : il possède des commandes associées",
                exception.getMessage()
        );

        verify(clientRepository).findById(1L);

        verify(clientRepository, never())
                .delete(any(Client.class));
    }

    @Test
    void supprimerMonProfil_clientInexistant_leveException() {

        when(authentication.getName())
                .thenReturn("999");

        when(clientRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> clientService.supprimerMonProfil(authentication)
        );

        assertEquals("Client introuvable", exception.getMessage());

        verify(authentication).getName();
        verify(clientRepository).findById(999L);

        verify(clientRepository, never())
                .delete(any(Client.class));
    }

    @Test
    void modifierClient_clientEssaieModifierAutreClient_leveException() {

        Client client = new Client();
        client.setId(2L);
        client.setNom("Autre Client");
        client.setEmail("autre@test.com");

        when(authentication.getName())
                .thenReturn("1");

        when(clientRepository.findById(2L))
                .thenReturn(java.util.Optional.of(client));

        UpdateClientRequest request = new UpdateClientRequest(
                "Seydina",
                "seydina@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> clientService.modifierClient(2L, authentication, request)
        );

        verify(authentication).getName();
        verify(clientRepository).findById(2L);

        verify(clientMapper, never())
                .updateEntity(any(Client.class), any(UpdateClientRequest.class));

        verify(clientRepository, never())
                .save(any(Client.class));
    }

    @Test
    void modifierClient_adminModifieClient_avecSucces() {

        Client client = new Client();
        client.setId(2L);
        client.setNom("Ancien Nom");
        client.setEmail("client@test.com");
        client.setTelephone("771234567");
        client.setAdresse("Dakar");
        client.setDateNaissance(LocalDate.of(2000, 5, 15));

        when(clientRepository.findById(2L))
                .thenReturn(java.util.Optional.of(client));

        Collection<GrantedAuthority> authorities = java.util.List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        doReturn(authorities)
                .when(authentication)
                .getAuthorities();


        UpdateClientRequest request = new UpdateClientRequest(
                "Nouveau Nom",
                "client@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        when(clientRepository.save(client))
                .thenReturn(client);

        ClientResponse response = new ClientResponse(
                2L,
                "Nouveau Nom",
                "client@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        when(clientMapper.toResponse(client))
                .thenReturn(response);

        ClientResponse result =
                clientService.modifierClient(2L, authentication, request);

        assertEquals(2L, result.getId());
        assertEquals("Nouveau Nom", result.getNom());
        assertEquals("client@test.com", result.getEmail());
        assertEquals("776543210", result.getTelephone());
        assertEquals("Pikine", result.getAdresse());

        verify(clientRepository).findById(2L);
        verify(clientMapper).updateEntity(client, request);
        verify(clientRepository).save(client);
        verify(clientMapper).toResponse(client);
        verify(authentication).getAuthorities();
    }

    @Test
    void modifierClient_clientModifieSonPropreProfil_avecSucces() {

        Client client = new Client();
        client.setId(2L);
        client.setNom("Ancien Nom");
        client.setEmail("client@test.com");
        client.setTelephone("771234567");
        client.setAdresse("Dakar");
        client.setDateNaissance(LocalDate.of(2000, 5, 15));

        when(clientRepository.findById(2L))
                .thenReturn(java.util.Optional.of(client));

        Collection<GrantedAuthority> authorities = java.util.List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        );

        doReturn(authorities)
                .when(authentication)
                .getAuthorities();

        when(authentication.getName())
                .thenReturn("2");

        UpdateClientRequest request = new UpdateClientRequest(
                "Nouveau Nom",
                "client@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        when(clientRepository.save(client))
                .thenReturn(client);

        ClientResponse response = new ClientResponse(
                2L,
                "Nouveau Nom",
                "client@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        when(clientMapper.toResponse(client))
                .thenReturn(response);

        ClientResponse result =
                clientService.modifierClient(2L, authentication, request);

        assertEquals(2L, result.getId());
        assertEquals("Nouveau Nom", result.getNom());
        assertEquals("client@test.com", result.getEmail());
        assertEquals("776543210", result.getTelephone());
        assertEquals("Pikine", result.getAdresse());

        verify(clientRepository).findById(2L);
        verify(authentication).getAuthorities();
        verify(authentication).getName();
        verify(clientMapper).updateEntity(client, request);
        verify(clientRepository).save(client);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void chercherMonProfil_clientInexistant_leveException() {

        when(authentication.getName())
                .thenReturn("999");

        when(clientRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> clientService.chercherMonProfil(authentication)
        );

        assertEquals("Client introuvable", exception.getMessage());

        verify(authentication).getName();
        verify(clientRepository).findById(999L);
        verify(clientMapper, never()).toResponse(any(Client.class));
    }

    @Test
    void modifierMonProfil_clientInexistant_leveException() {

        when(authentication.getName())
                .thenReturn("999");

        when(clientRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        UpdateClientRequest request = new UpdateClientRequest(
                "Seydina",
                "seydina@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> clientService.modifierMonProfil(authentication, request)
        );

        assertEquals("Client introuvable", exception.getMessage());

        verify(authentication).getName();
        verify(clientRepository).findById(999L);

        verify(clientRepository, never())
                .save(any(Client.class));

        verify(clientMapper, never())
                .updateEntity(any(Client.class), any(UpdateClientRequest.class));
    }

    @Test
    void modifierClient_clientInexistant_leveException() {

        when(clientRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        UpdateClientRequest request = new UpdateClientRequest(
                "Seydina",
                "seydina@test.com",
                "776543210",
                "Pikine",
                LocalDate.of(2000, 5, 15)
        );

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> clientService.modifierClient(999L, authentication, request)
        );

        assertEquals("Client introuvable", exception.getMessage());

        verify(clientRepository).findById(999L);

        verify(clientRepository, never())
                .save(any(Client.class));

        verify(clientMapper, never())
                .updateEntity(any(Client.class), any(UpdateClientRequest.class));
    }

    @Test
    void supprimerClient_clientEssaieSupprimerAutreClient_leveException() {

        Client client = new Client();
        client.setId(2L);
        client.setNom("Autre Client");
        client.setEmail("autre@test.com");

        when(clientRepository.findById(2L))
                .thenReturn(java.util.Optional.of(client));

        doReturn(java.util.List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        )).when(authentication).getAuthorities();

        when(authentication.getName())
                .thenReturn("1");

        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> clientService.supprimerClient(2L, authentication)
        );

        verify(clientRepository).findById(2L);
        verify(authentication).getAuthorities();
        verify(authentication).getName();

        verify(clientRepository, never())
                .delete(any(Client.class));
    }

    @Test
    void supprimerClient_adminSupprimeClient_sansCommande() {

        Client client = new Client();
        client.setId(2L);
        client.setNom("Client");
        client.setEmail("client@test.com");

        when(clientRepository.findById(2L))
                .thenReturn(java.util.Optional.of(client));

        doReturn(java.util.List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        )).when(authentication).getAuthorities();

        clientService.supprimerClient(2L, authentication);

        verify(clientRepository).findById(2L);
        verify(authentication).getAuthorities();
        verify(clientRepository).delete(client);
    }

    @Test
    void supprimerClient_avecCommandes_leveException() {

        Client client = new Client();
        client.setId(2L);
        client.setNom("Client");
        client.setEmail("client@test.com");

        Commande commande = new Commande();
        client.getCommandes().add(commande);

        when(clientRepository.findById(2L))
                .thenReturn(java.util.Optional.of(client));

        doReturn(java.util.List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        )).when(authentication).getAuthorities();

        ClientHasCommandesException exception = assertThrows(
                ClientHasCommandesException.class,
                () -> clientService.supprimerClient(2L, authentication)
        );

        assertEquals(
                "Impossible de supprimer le client : il possède des commandes associées",
                exception.getMessage()
        );

        verify(clientRepository).findById(2L);
        verify(authentication).getAuthorities();

        verify(clientRepository, never())
                .delete(any(Client.class));
    }

    @Test
    void supprimerClient_clientInexistant_leveException() {

        when(clientRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> clientService.supprimerClient(999L, authentication)
        );

        assertEquals("Client introuvable", exception.getMessage());

        verify(clientRepository).findById(999L);

        verify(clientRepository, never())
                .delete(any(Client.class));
    }
}
