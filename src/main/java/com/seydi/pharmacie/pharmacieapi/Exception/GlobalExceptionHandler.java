package com.seydi.pharmacie.pharmacieapi.Exception;

import com.seydi.pharmacie.pharmacieapi.dto.response.ApiError;
import com.seydi.pharmacie.pharmacieapi.dto.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Erreur métier si le client n'existe pas
    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ApiError> gererClientIntrouvable(ClientNotFoundException ex) {

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.NOT_FOUND
        );
    }

    //erreur metier si le produit n'existe pas
    @ExceptionHandler(ProduitNotFoundException.class)
    public ResponseEntity<ApiError> gererProduitIntrouvable(ProduitNotFoundException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.NOT_FOUND
        );
    }

    //Erreur métier si le stock n'éxiste pas
    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<ApiError> gererStockIntrouvable(StockNotFoundException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.NOT_FOUND
        );
    }

    //Erreur métier si le fournisseur n'éxiste pas
    @ExceptionHandler(FournisseurNotFoundException.class)
    public ResponseEntity<ApiError> gererFournisseurIntrouvable(FournisseurNotFoundException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.NOT_FOUND
        );
    }

    //Erreur de Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> gererValidation(MethodArgumentNotValidException ex) {

        //La clé (String) est le nom du champ, et la valeur (String) est le message d'erreur.
        Map<String, String> erreurs = new HashMap<>();

        // getFieldErrors() renvoie uniquement les erreurs liées aux champs (nom, email, etc.).
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        ValidationErrorResponse erreur = new ValidationErrorResponse(
                "Erreur de validation",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                erreurs
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.BAD_REQUEST
        );
    }

    //Si l'email est deja utilise
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> gererEmailDejaExistant(EmailAlreadyExistsException ex) {

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.CONFLICT
        );
    }

    // si le nom du produit exist déja
    @ExceptionHandler(ProduitAlreadyExistsException.class)
    public ResponseEntity<ApiError> gererNomProduitDejaExistant(ProduitAlreadyExistsException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.CONFLICT
        );
    }

    //Si le stock éxiste déja
    @ExceptionHandler(StockAlreadyExistsException.class)
    public ResponseEntity<ApiError> gererStockDejaExistant(StockAlreadyExistsException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.CONFLICT
        );
    }

    //Si le fournisseur éxiste déja
    @ExceptionHandler(FournisseurAlreadyExistsException.class)
    public ResponseEntity<ApiError> gererFournisseurDejaExistant(FournisseurAlreadyExistsException ex){

        ApiError erreur =  new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.CONFLICT
        );
    }

    //si le fournisseur a des produits qui lui sont encore associé
    @ExceptionHandler(FournisseurHasProductsException.class)
    public ResponseEntity<ApiError> gererFournisseurAvecProduits(FournisseurHasProductsException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.CONFLICT
        );
    }

    //Erreur métier si le produit possède encore un stock
    @ExceptionHandler(ProduitHasStockException.class)
    public ResponseEntity<ApiError> gererProduitAvecStock(ProduitHasStockException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.CONFLICT
        );
    }
}
