package com.seydi.pharmacie.pharmacieapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateClientRequest {

    //on met les infos autorise a modifier

    @NotBlank(message = "Nom obligatoire")
    @Size(min = 2,max = 50,message = "Le nom doit contenir au moins 2 caractéres")
    private String nom;

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Telephone obligatoire")
    private String telephone;

    @NotBlank(message = "Adresse obligatoire")
    @Size(min = 5,max = 150, message = "L'adresse doit contenir entre 5 et 150 caractères.")
    private String adresse;

    public UpdateClientRequest(){}

    public UpdateClientRequest(String nom, String email,String telephone, String adresse) {
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "UpdateClientRequest{" +
                "nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
