package com.seydi.pharmacie.pharmacieapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateClientRequest {

    //On met uniquement les champs que le client est autorisé ou doit fournir.

    @NotBlank(message = "Nom obligatoire")
    @Size(min = 2,max = 50,message = "Le nom doit contenir au moins 2 caractéres")
    @Schema(
            description = "Nom complet du client",
            example = "Mamadou Diop"
    )
    private String nom;

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email Invalide")
    @Schema(
            description = "Adresse email du client",
            example = "mamadou.diop@gmail.com"
    )
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 8,max = 100,message = "Le mot de passe doit contenir au moins 8 caractères")
    @Schema(
            description = "Mot de passe du compte, au minimum 8 caractères",
            example = "********"
    )
    private String motDePasse;

    @NotBlank(message = "Telephone obligatoire")
    @Pattern(
            regexp = "^(70|75|76|77|78)[0-9]{7}$",
            message = "Numéro de téléphone sénégalais invalide."
    )
    @Schema(
            description = "Numéro de téléphone sénégalais",
            example = "771234567"
    )
    private String telephone;

    @NotBlank(message = "Adresse obligatoire")
    @Size(min = 4, message = "Adresse doit contenir au moins 4 caractéres")
    @Schema(
            description = "Adresse du client",
            example = "Dakar, Sénégal"
    )
    private String adresse;

    public CreateClientRequest() {}

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

    //Il faut qu'on retire le mot de passe du toString(). Même si on le hash ensuite,
    // le DTO contient le mot de passe en clair pendant la requête et il ne faut pas risquer de le retrouver dans les logs.
    @Override
    public String toString() {
        return "CreateClientRequest{" +
                "nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
