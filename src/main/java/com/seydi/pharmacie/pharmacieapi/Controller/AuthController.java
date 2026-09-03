package com.seydi.pharmacie.pharmacieapi.controller;

import com.seydi.pharmacie.pharmacieapi.dto.request.LoginRequest;
import com.seydi.pharmacie.pharmacieapi.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    //authentification
    @Operation(
            summary = "Authentifier un client",
            description = "Authentifie un utilisateur à partir de son email et de son mot de passe et retourne un JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification réussie"),
            @ApiResponse(responseCode = "400", description = "Données de connexion invalides")
    })
    @SecurityRequirements //pour dire que cette méthode ne nécéssite pas d'etre authentifier
    @PostMapping
    public String authentifierClient(@Valid @RequestBody LoginRequest request){
        return authenticationService.login(request);
    }
}
