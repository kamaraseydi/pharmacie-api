package com.seydi.pharmacie.pharmacieapi.Controller;

import com.seydi.pharmacie.pharmacieapi.dto.request.LoginRequest;
import com.seydi.pharmacie.pharmacieapi.security.AuthenticationService;
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
    @PostMapping
    public String authentifierClient(@Valid @RequestBody LoginRequest request){
        return authenticationService.login(request);
    }
}
