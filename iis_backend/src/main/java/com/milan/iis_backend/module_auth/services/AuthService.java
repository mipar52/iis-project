package com.milan.iis_backend.module_auth.services;

import com.milan.iis_backend.model.users.AppUser;
import com.milan.iis_backend.repository.AppUserRepository;
import com.milan.iis_backend.repository.RefreshTokenRepository;
import com.milan.iis_backend.module_auth.jwt.JWTService;
import com.milan.iis_backend.module_auth.PasswordConfig;
import com.milan.iis_backend.module_auth.models.RefreshToken;
import com.milan.iis_backend.module_auth.models.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final long refreshTtlSeconds;

    public AuthService(
            AppUserRepository appUserRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JWTService jwtService,
            @Value("${jwt.refresh.ttlSeconds}") long refreshTtlSeconds) {
        this.appUserRepository = appUserRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    public TokenResponse register(String username, String password, String role) {
        System.out.println("GOT ROLE: " + role);
        Optional<AppUser> appUser = appUserRepository.findByUsername(username);
        if (appUser.isPresent()) throw new RuntimeException("User already exists!");
        AppUser newAppUser = new AppUser();
        newAppUser.setUsername(username);

        String normalized = (role == null || role.isBlank()) ? "USER" : role.trim().toUpperCase();
        if (!normalized.equals("USER") && !normalized.equals("ADMIN")) {
            throw new RuntimeException("Invalid role");
        }
        newAppUser.setRole(normalized);

        PasswordConfig config = new PasswordConfig();
        newAppUser.setPasswordHash(config.passwordEncoder().encode(password));
        appUserRepository.save(newAppUser);

        // after register, make login to save time :)
        return login(username, password);
    }

    public TokenResponse login(String username, String password) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Bad credentials!"));

        if (!passwordEncoder.matches(password, appUser.getPasswordHash())) {
            throw new RuntimeException("Bad credentials!");
        }

        String access = jwtService.generateAccessToken(appUser);
        String refresh = issueRefreshToken(appUser);
        return new TokenResponse(access, refresh);
    }

    public TokenResponse refresh(String refreshTokenValue) {
        System.out.println("Getting refresh token: " + refreshTokenValue);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> (new RuntimeException("Invalid refresh token!")));

        if (refreshToken.isRevoked()) throw new RuntimeException("Refresh token revoked!");
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) throw new RuntimeException("Refresh token expired!");

        String newAccessToken = jwtService.generateAccessToken(refreshToken.getUser());

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        String newRefreshToken = issueRefreshToken(refreshToken.getUser());
        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void revokeRefreshToken(String refreshTokenValue) {
        System.out.println("Revoking token: " + refreshTokenValue);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue).orElseThrow(() -> new RuntimeException("Invalid refresh token!"));
        if (refreshToken.isRevoked()) throw new RuntimeException("Refresh token revoked!");

        refreshToken.setRevoked(true);
        refreshToken.setExpiresAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }

    private String issueRefreshToken(AppUser appUser) {
        String token = randomToken(48);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(appUser);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshTtlSeconds));
        refreshToken.setRevoked(false);
        System.out.println("Issued refresh token: " + refreshToken.getToken());
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    private static String randomToken(int bytes) {
        byte[] b = new byte[bytes];
        new SecureRandom().nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

}
