package com.seydi.pharmacie.pharmacieapi.mapper;

import com.seydi.pharmacie.pharmacieapi.Model.Client;
import com.seydi.pharmacie.pharmacieapi.dto.request.CreateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.request.UpdateClientRequest;
import com.seydi.pharmacie.pharmacieapi.dto.response.ClientResponse;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    //Ici on met ce qu'on ajoute dans le CreateClient
    public Client toEntity(CreateClientRequest request) {
        Client client = new Client();

        client.setNom(request.getNom());
        client.setEmail(request.getEmail());
        client.setMotDePasse(request.getMotDePasse());
        client.setTelephone(request.getTelephone());
        client.setAdresse(request.getAdresse());

        return client;
    }

    //Ici on ajoute ce qu'on donne comme reponse dans le ClientResponse
    public ClientResponse toResponse(Client client) {
        ClientResponse response = new ClientResponse();

        response.setId(client.getId());
        response.setNom(client.getNom());
        response.setEmail(client.getEmail());
        response.setTelephone(client.getTelephone());
        response.setAdresse(client.getAdresse());

        return response;
    }

    //ce qu'on donne lors de la modification
    public void updateEntity(Client client, UpdateClientRequest request){

        //ici pas besoin de créer un nouveau client on modifie juste les données existant

        client.setNom(request.getNom());
        client.setEmail(request.getEmail());
        client.setTelephone(request.getTelephone());
        client.setAdresse(request.getAdresse());

    }

}
