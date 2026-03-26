package com.milan.iis_backend.security.jwt;

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

    public String generateAccessToken(String username) {
        Instant now = Instant.now();
        Instant expiryTime = now.plusSeconds(accessTtlSeconds);

        return Jwts.builder()
                .subject(username)
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
}
