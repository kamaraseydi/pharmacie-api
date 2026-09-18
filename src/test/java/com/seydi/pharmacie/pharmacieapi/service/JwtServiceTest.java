package com.seydi.pharmacie.pharmacieapi.service;

import com.seydi.pharmacie.pharmacieapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void generateToken_authentificationValide_retourneToken() {

        when(authentication.getName())
                .thenReturn("1");

        doReturn(List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        )).when(authentication).getAuthorities();

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        when(jwt.getTokenValue())
                .thenReturn("jwt-token-test");

        String result = jwtService.generateToken(authentication);

        assertEquals("jwt-token-test", result);

        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
        verify(jwt).getTokenValue();
    }

    @Test
    void generateToken_ajouteLesBonnesClaims() {

        when(authentication.getName())
                .thenReturn("42");

        doReturn(List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        )).when(authentication).getAuthorities();

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        when(jwt.getTokenValue())
                .thenReturn("jwt-token-test");

        jwtService.generateToken(authentication);

        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(captor.capture());

        JwtClaimsSet claims = captor.getValue().getClaims();

        assertEquals("42", claims.getSubject());
        assertEquals("ROLE_CLIENT", claims.getClaimAsString("role"));

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiresAt());

        assertTrue(
                claims.getExpiresAt().isAfter(claims.getIssuedAt())
        );
    }


}