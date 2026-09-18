package com.seydi.pharmacie.pharmacieapi.service;

import com.seydi.pharmacie.pharmacieapi.dto.request.LoginRequest;
import com.seydi.pharmacie.pharmacieapi.security.AuthenticationService;
import com.seydi.pharmacie.pharmacieapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void login_avecIdentifiantsValides_retourneToken() {

        LoginRequest request = new LoginRequest(
                "seydina@test.com",
                "Test1234!"
        );

        String token = "jwt-token-test";

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtService.generateToken(authentication))
                .thenReturn(token);

        String result = authenticationService.login(request);

        assertEquals(token, result);

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(authentication);
    }

    @Test
    void login_avecIdentifiantsInvalides_leveException() {

        LoginRequest request = new LoginRequest(
                "seydina@test.com",
                "MauvaisMotDePasse"
        );

        org.springframework.security.core.AuthenticationException exception =
                new org.springframework.security.authentication.BadCredentialsException(
                        "Identifiants invalides"
                );

        when(authenticationManager.authenticate(any()))
                .thenThrow(exception);

        assertThrows(
                org.springframework.security.core.AuthenticationException.class,
                () -> authenticationService.login(request)
        );

        verify(authenticationManager).authenticate(any());

        verify(jwtService, never())
                .generateToken(any());
    }


}
