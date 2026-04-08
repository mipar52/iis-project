package com.milan.iis_backend.module_auth.jwt;

import com.milan.iis_backend.model.users.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JWTService {
    private final SecretKey secretKey;
    private final long accessTtlSeconds;

    public JWTService(@Value("${jwt.secret}") String secret, @Value("${jwt.access.ttlSeconds}") long accessTtlSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
    }

    public String generateAccessToken(AppUser appUser) {
        Instant now = Instant.now();
        Instant expiryTime = now.plusSeconds(accessTtlSeconds);

        return Jwts.builder()
                .subject(appUser.getUsername())
                .claim("role", appUser.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryTime))
                .signWith(secretKey)
                .compact();
    }

    public String validateAndGetSubject(String jwt) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    public String validateAndGetRole(String token) {
        Object roleObject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role");

        return roleObject != null ? roleObject.toString() : null;
    }
}
