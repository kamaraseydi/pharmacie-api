package com.seydi.pharmacie.pharmacieapi.dto.request;

import jakarta.validation.constraints.*;

public class CreateFournisseurRequest {

    @NotBlank(message = "Nom obligatoire")
    @Size(min = 2,message = "Le nom doit contenir au minimum 2 caractéres")
    private String nom;

    @NotBlank(message = "Adresse obligatoire")
    @Size(min = 4, message = "L'adresse doit contenir au minimum 4 caractéres")
    private String adresse;

    @NotNull(message = "Numéro Téléphone obligatoire")
    @Pattern(
            regexp = "^(70|75|76|77|78)[0-9]{7}$",
            message = "Numéro de téléphone sénégalais invalide."
    )
    private String telephone;

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    private String email;

    public CreateFournisseurRequest(){}

    public CreateFournisseurRequest(String nom, String adresse, String telephone, String email) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "CreateFournisseurRequest{" +
                "nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
