package com.seydi.pharmacie.pharmacieapi.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.seydi.pharmacie.pharmacieapi.security.ClientDetailsService;
import com.seydi.pharmacie.pharmacieapi.security.RestAccessDeniedHandler;
import com.seydi.pharmacie.pharmacieapi.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;


//Cette classe contient de la configuration Spring et peut déclarer des objets que Spring doit gérer.
@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    //pour les erreurs 401 genre non authentifié quoi

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint, RestAccessDeniedHandler accessDeniedHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    //L'objet retourné par cette méthode doit être géré par Spring.
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //PasswordEncoder : C'est l'interface que notre application va utiliser pour gérer les mots de passe.
    //

    //BCryptPasswordEncoder est une implémentation de PasswordEncoder: Spring Security recommande des fonctions
    // de hashage adaptatives
    //comme BCrypt, PBKDF2, scrypt ou Argon2 pour le stockage des mots de passe ; BCrypt est l'une des implémentations disponibles
    // et est volontairement coûteuse à calculer pour rendre les attaques par essais plus difficiles.

    @Bean
    public DaoAuthenticationProvider authenticationProvider(ClientDetailsService clientDetailsService,
                                                            PasswordEncoder passwordEncoder){
        // configurer le provider
        //1-> Je crée le composant qui va être chargé de vérifier l'authentification.
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(clientDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    //C'est un objet fourni par Spring Security qui permet de récupérer l'AuthenticationManager configuré par Spring.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    //pour recupérer le role
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            String role = jwt.getClaimAsString("role");

            return List.of(new SimpleGrantedAuthority(role));
        });

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Authentification
                        .requestMatchers("/auth").permitAll()

                        // Inscription
                        .requestMatchers(HttpMethod.POST, "/clients").permitAll()

                        // Clients
                        .requestMatchers(HttpMethod.GET, "/clients").hasRole("ADMIN")

                        // Consulter son propre profil
                        .requestMatchers(HttpMethod.GET, "/clients/me").hasAnyRole("ADMIN", "CLIENT")

                        //Rechercher client
                        .requestMatchers(HttpMethod.GET, "/clients/**").hasRole("ADMIN")

                        //Modifier mon profil
                        .requestMatchers(HttpMethod.PUT, "/clients/me").hasAnyRole("ADMIN","CLIENT")

                        //modifier client précis
                        .requestMatchers(HttpMethod.PUT, "/clients/**").hasRole("ADMIN")

                        //Supprimer son profil
                        .requestMatchers(HttpMethod.DELETE, "/clients/me").hasRole("CLIENT")

                        //Supp client
                        .requestMatchers(HttpMethod.DELETE, "/clients/**").hasRole("ADMIN")

                        // Produits
                        .requestMatchers(HttpMethod.GET, "/produits", "/produits/**")
                        .hasAnyRole("CLIENT", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/produits")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/produits/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/produits/**")
                        .hasRole("ADMIN")

                        //Ajout commande
                        .requestMatchers(HttpMethod.POST, "/commandes").hasRole("CLIENT")

                        //Voir commande
                        .requestMatchers(HttpMethod.GET, "/commandes", "/commandes/**").hasAnyRole("CLIENT","ADMIN")

                        //modifier statut commande
                        .requestMatchers(HttpMethod.PUT, "/commandes/*/statut").hasRole("ADMIN")

                        //annuler commande
                        .requestMatchers(HttpMethod.PUT, "/commandes/*/annuler").hasAnyRole("ADMIN","CLIENT")

                        //Ajouter fournisseur
                        .requestMatchers(HttpMethod.POST, "/fournisseurs").hasRole("ADMIN")

                        //Lister fournisseurs et rechercher
                        .requestMatchers(HttpMethod.GET, "/fournisseurs", "/fournisseurs/**").hasRole("ADMIN")

                        //modifier fournisseur
                        .requestMatchers(HttpMethod.PUT, "/fournisseurs/**").hasRole("ADMIN")

                        //Supprimer fournisseur
                        .requestMatchers(HttpMethod.DELETE, "/fournisseurs/**").hasRole("ADMIN")

                        //Ajout de stock
                        .requestMatchers(HttpMethod.POST, "/stocks").hasRole("ADMIN")

                        //Lister stocks et rechercher stocks
                        .requestMatchers(HttpMethod.GET, "/stocks", "/stocks/**").hasRole("ADMIN")

                        //Modifier stock
                        .requestMatchers(HttpMethod.PUT, "/stocks/**").hasRole("ADMIN")

                        //Supp stock
                        .requestMatchers(HttpMethod.DELETE, "/stocks/**").hasRole("ADMIN")

                        // Tout le reste
                        .anyRequest().authenticated()


                ).exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)

                ).oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }
}
