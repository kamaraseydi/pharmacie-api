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

    // Erreur métier si le clientg n'existe pas
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
}
