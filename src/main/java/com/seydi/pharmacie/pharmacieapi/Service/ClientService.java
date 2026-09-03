package com.seydi.pharmacie.pharmacieapi.service;

import com.seydi.pharmacie.pharmacieapi.exception.ClientHasCommandesException;
import com.seydi.pharmacie.pharmacieapi.exception.ClientNotFoundException;
import com.seydi.pharmacie.pharmacieapi.exception.EmailAlreadyExistsException;
import com.seydi.pharmacie.pharmacieapi.model.Client;
import com.seydi.pharmacie.pharmacieapi.model.Role;
import com.seydi.pharmacie.pharmacieapi.repository.ClientRepository;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ClientResponse;
import com.seydi.pharmacie.pharmacieapi.mapper.ClientMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {


    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper,PasswordEncoder passwordEncoder){
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.passwordEncoder = passwordEncoder;
    }

    private Client trouverClientOuLeverException(Long id){
        return clientRepository.findById(id) //Cherche le client.
                .orElseThrow(()-> new ClientNotFoundException("Client introuvable")); //sinon lance une exception
    }


    //Ici on a pas besoin de faire une double verif puisque le controle est deja effectue dans securityConfig
    public List<ClientResponse> listerClients() {

        return clientRepository.findAll() //Récupère tous les clients.
                .stream() //Fais-les passer dans un flux.
                .map(clientMapper::toResponse) //Transforme chaque client en ClientResponse.
                .toList(); //Remets le résultat dans une liste.

    }

    /*
    return clientRepository.findAll()
        .stream()
        .filter(client -> client.getNom().startsWith("S"))
        .map(clientMapper::toResponse)
        .toList();

       Récupère tous les clients →
       garde seulement ceux dont le nom commence par S →
       transforme-les en ClientResponse →
       remets-les dans une liste.

       Tu remarques quelque chose ?

       On a utilisé filter() avant map().

       Pourquoi ?

       Parce qu'il est plus logique de filtrer les entités d'abord,
       puis de transformer uniquement celles qui nous intéressent. C'est aussi un peu plus performant.
     */

    //Methode pour ajouter un client
    public ClientResponse ajouterClient(CreateClientRequest request) {

        Client client = clientMapper.toEntity(request);

        //info auto du client
        client.setRole(Role.CLIENT);

        //verifier si l'email n'est pas deja utilisé
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new EmailAlreadyExistsException("Email déja utilisé");
        }

        //Sécuriser le password
        String password = passwordEncoder.encode(client.getMotDePasse());

        //Remettre le password sécurisé
        client.setMotDePasse(password);

        // ajout du client
        Client clientSauvegarde = clientRepository.save(client);
        // Retourner un DTO au frontend.
        return clientMapper.toResponse(clientSauvegarde);
    }

   /*
   //1ere methode pour cherche un client à partir de son id

   public Client chercherClientParId(Long id){

      Optional<Client> client = clientRepository.findById(id);

      if(client.isPresent()){
         return client.get();
      }else {
            throw new ClientNotFoundException("Client introuvable");
      }
   }
   */

    //Vérifier si l'utilisateur connecté est un admin
    private boolean estAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority()
                        .equals("ROLE_ADMIN"));
    }

    //Vérifier qui aura accés au profil du client
    private void verifierAccesProfilClient(Client client, Authentication authentication) throws AccessDeniedException {
        if(estAdmin(authentication)){
            return;
        }

        if(!client.getId().equals(Long.valueOf(authentication.getName()))){
            throw new AccessDeniedException("Accés refusé");
        }

    }

    //2eme methode plus moderne
    public ClientResponse chercherClientParId(Long id,Authentication authentication) {

        Client client = trouverClientOuLeverException(id);

        verifierAccesProfilClient(client,authentication);

        // S'il existe Transforme le Client en ClientResponse
        return clientMapper.toResponse(client);

    }

    //Chercher le profil du client connecter
    public ClientResponse chercherMonProfil(Authentication authentication){

        // récupérer l'identité depuis le JWT
        Long id = Long.valueOf(authentication.getName());

        // récupérer le client
        Client clientConnecter = trouverClientOuLeverException(id);

        // transformer en ClientResponse
        return clientMapper.toResponse(clientConnecter);
    }

    public ClientResponse modifierMonProfil(Authentication authentication, UpdateClientRequest request) {

        // récupérer l'identité depuis le JWT
        Long id = Long.valueOf(authentication.getName());

        // récupérer le client
        Client clientConnecter = trouverClientOuLeverException(id);

        // 2. Vérifier que le nouvel email on la changer et s'il n'est pas déjà utilisé
        // par un autre client.

        if(!clientConnecter.getEmail().equalsIgnoreCase(request.getEmail())){
            if(clientRepository.existsByEmail(request.getEmail())){
                throw new EmailAlreadyExistsException("Email déja utilisé");
            }
        }

        // 3. Mettre à jour l'objet Client existant
        // avec les nouvelles informations reçues.
        // Aucun nouvel objet Client n'est créé.
        clientMapper.updateEntity(clientConnecter, request);

        // 4. Sauvegarder les modifications dans la base.
        Client clientSauvegarde = clientRepository.save(clientConnecter);

        // 5. Retourner un DTO au frontend.
        return clientMapper.toResponse(clientSauvegarde);

    }

    public ClientResponse modifierClient(Long id,Authentication authentication, UpdateClientRequest request) {


        Client clientExistant = trouverClientOuLeverException(id);

        // vérifier l'accés
        verifierAccesProfilClient(clientExistant,authentication);

        // 2. Vérifier que le nouvel email on la changer et s'il n'est pas déjà utilisé
        // par un autre client.

        if(!clientExistant.getEmail().equalsIgnoreCase(request.getEmail())){
            if(clientRepository.existsByEmail(request.getEmail())){
                throw new EmailAlreadyExistsException("Email déja utilisé");
            }
        }

        // 3. Mettre à jour l'objet Client existant
        // avec les nouvelles informations reçues.
        // Aucun nouvel objet Client n'est créé.
        clientMapper.updateEntity(clientExistant, request);

        // 4. Sauvegarder les modifications dans la base.
        Client clientSauvegarde = clientRepository.save(clientExistant);

        // 5. Retourner un DTO au frontend.
        return clientMapper.toResponse(clientSauvegarde);

    }

    //Methode pour vérifier si la supprésion est valide
    private void verifierSuppressionClient(Client client) {

        if (!client.getCommandes().isEmpty()) {
            throw new ClientHasCommandesException(
                    "Impossible de supprimer le client : il possède des commandes associées"
            );
        }
    }

    //Supprimer SON PROFIL
    public void supprimerMonProfil(Authentication authentication) {

        //Chercher le client
        Long id = Long.valueOf(authentication.getName());

        //Récupérer le client
        Client clientConnecter = trouverClientOuLeverException(id);

        verifierSuppressionClient(clientConnecter);

        clientRepository.delete(clientConnecter);
    }


    //Supprimer un client
    public void supprimerClient(Long id,Authentication authentication) {

        //Chercher le client
       Client client =  trouverClientOuLeverException(id);

       //Vérifier si l'utilisateur connecter est parmis d'accés ici
        verifierAccesProfilClient(client,authentication);

        verifierSuppressionClient(client);

        clientRepository.delete(client);
    }
}
