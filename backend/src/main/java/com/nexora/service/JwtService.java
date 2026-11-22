package com.nexora.service;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

   private static final String SECRET_KEY =
        System.getenv("NEXORA_JWT_SECRET");

    private static final long EXPIRATION_TIME =
            1000 * 60 * 60 * 24;

    private Key getSigningKey() {

    if (SECRET_KEY == null || SECRET_KEY.isBlank()) {
        throw new IllegalStateException(
            "NEXORA_JWT_SECRET environment variable is not configured."
        );
    }

    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

    return Keys.hmacShaKeyFor(keyBytes);
}

    public String generateToken(String email, String role) {

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {

        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {

        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token, String email) {

        String tokenEmail = extractEmail(token);

        return tokenEmail.equals(email)
                && extractAllClaims(token).getExpiration().after(new Date());
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
