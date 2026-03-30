package com.milan.iis_backend.controller;

import com.milan.iis_backend.security.auth.LoginRequest;
import com.milan.iis_backend.security.auth.RefreshRequest;
import com.milan.iis_backend.security.auth.RegisterRequest;
import com.milan.iis_backend.security.auth.TokenResponse;
import com.milan.iis_backend.security.auth.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public TokenResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request.getUsername(), request.getPassword(), request.getRole());
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        return authService.login(request.getUsername(), request.getPassword());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshRequest request) {
        log.info("Calling public TokenResponse refresh(@RequestBody RefreshRequest request)");
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/revoke")
    public void revoke(@RequestBody RefreshRequest request) {
        authService.revokeRefreshToken(request.getRefreshToken());
    }
}
