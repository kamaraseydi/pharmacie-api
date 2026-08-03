package com.seydi.pharmacie.pharmacieapi.Service;

import com.seydi.pharmacie.pharmacieapi.Exception.ClientNotFoundException;
import com.seydi.pharmacie.pharmacieapi.Exception.EmailAlreadyExistsException;
import com.seydi.pharmacie.pharmacieapi.Model.Client;
import com.seydi.pharmacie.pharmacieapi.Repository.ClientRepository;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ClientResponse;
import com.seydi.pharmacie.pharmacieapi.mapper.ClientMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {


    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    private Client trouverClientOuLeverException(Long id){
        return clientRepository.findById(id) //Cherche le client.
                .orElseThrow(()-> new ClientNotFoundException("Client introuvable")); //sinon lance une exception
    }

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public List<ClientResponse> listerClients() {

        return clientRepository.findAll() //Récupère tous les clients.
                .stream() //Fais-les passer dans un flux.
                .map(client -> clientMapper.toResponse(client)) //Transforme chaque client en ClientResponse.
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

        //verifier si l'email n'est pas deja utilisé
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new EmailAlreadyExistsException("Email déja utilisé");
        }
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


    //2eme methode plus moderne
    public ClientResponse chercherClientParId(Long id) {

        // S'il existe Transforme le Client en ClientResponse
        return clientMapper.toResponse(trouverClientOuLeverException(id));

    }

    public ClientResponse modifierClient(Long id, UpdateClientRequest request) {

        // 1. Rechercher le client dans la base.
        // Si aucun client ne possède cet id,
        // une ClientNotFoundException est levée.
        Client clientExistant = trouverClientOuLeverException(id);

        // 2. Vérifier que le nouvel email on la changer et s'il  n'est pas déjà utilisé
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

    //Supprimer un client
    public void supprimerClient(Long id) {

        //Chercher le client ici je n'ai pas besoin de l'objet client donc pas besoin de le stocker
        trouverClientOuLeverException(id);

        clientRepository.deleteById(id);
    }
}
