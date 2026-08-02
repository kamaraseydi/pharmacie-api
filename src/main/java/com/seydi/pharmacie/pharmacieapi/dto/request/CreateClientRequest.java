package com.seydi.pharmacie.pharmacieapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateClientRequest {

    //On met uniquement les champs que le client est autorisé ou doit fournir.

    @NotBlank(message = "Nom obligatoire")
    @Size(min = 2,max = 50,message = "Le nom doit contenir au moins 2 caractéres")
    private String nom;

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email Invalide")
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 8,max = 100,message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    @NotBlank(message = "Telephone obligatoire")
    @Pattern(
            regexp = "^(70|75|76|77|78)[0-9]{7}$",
            message = "Numéro de téléphone sénégalais invalide."
    )
    private String telephone;

    @NotBlank(message = "Adresse obligatoire")
    @Size(min = 5, message = "Adresse doit contenir au moins 5 caractéres")
    private String adresse;

    public CreateClientRequest() {

    }

    public CreateClientRequest(String nom, String email, String motDePasse, String telephone, String adresse) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
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

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
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
        return "CreateClientRequest{" +
                "nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
