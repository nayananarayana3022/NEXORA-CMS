
package com.nexora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;
import java.util.List;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nexora.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider)
            throws Exception {

    	http
        .cors(cors ->
            cors.configurationSource(corsConfigurationSource())
        )
        .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception ->
            exception.authenticationEntryPoint(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            )
        )

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))

            .authenticationProvider(authenticationProvider)

            .authorizeHttpRequests(auth -> auth

                // Registration and login are public
                .requestMatchers(
                    "/api/users/register",
                    "/api/users/login"
                ).permitAll()

                // Only ADMIN and EDITOR can approve or reject
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/movies/*/approve",
                    "/api/movies/*/reject",
                    "/api/articles/*/approve",
                    "/api/articles/*/reject"
                ).hasAnyRole("ADMIN", "EDITOR")

                // AUTHOR and ADMIN can submit content
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/movies/*/submit",
                    "/api/articles/*/submit"
                ).hasAnyRole("ADMIN", "AUTHOR")

                // Only ADMIN can delete content
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/movies/**",
                    "/api/articles/**"
                ).hasRole("ADMIN")

                // AUTHOR and ADMIN can add content
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/movies",
                    "/api/articles"
                ).hasAnyRole("ADMIN", "AUTHOR")

                // AUTHOR and ADMIN can update content
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/movies/*",
                    "/api/articles/*"
                ).hasAnyRole("ADMIN", "AUTHOR")

                // All other APIs require authentication
                
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://127.0.0.1:5500",
                "http://localhost:5500"
        ));

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type"
        ));

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
