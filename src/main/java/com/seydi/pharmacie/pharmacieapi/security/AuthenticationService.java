package com.seydi.pharmacie.pharmacieapi.security;

import com.seydi.pharmacie.pharmacieapi.dto.request.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    //prendre l'email + le mot de passe et demander à Spring Security de les authentifier.
    public String login(LoginRequest request){

        //Pour donner les identifiants à AuthenticationManager, Spring Security utilise un objet Authentication
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getMotDePasse()
        );

        // Puis on demande l'authentification avec réussie → Authentication retournée
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        return jwtService.generateToken(authentication);
    }
}
