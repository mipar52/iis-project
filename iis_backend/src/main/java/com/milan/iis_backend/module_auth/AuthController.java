package com.milan.iis_backend.module_auth;

import com.milan.iis_backend.module_auth.models.LoginRequest;
import com.milan.iis_backend.module_auth.models.RegisterRequest;
import com.milan.iis_backend.module_auth.models.TokenResponse;
import com.milan.iis_backend.module_auth.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private static final String REFRESH_COOKIE = "refreshToken";
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request, HttpServletResponse response) {
        TokenResponse tokens = authService.register(request.getUsername(), request.getPassword(), request.getRole());
        addRefreshCookie(response, tokens.getRefreshToken());
        return ResponseEntity.ok(new TokenResponse(tokens.getAccessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse tokens = authService.login(request.getUsername(), request.getPassword());
        addRefreshCookie(response, tokens.getRefreshToken());
        return ResponseEntity.ok(new TokenResponse(tokens.getAccessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken, HttpServletResponse response) {
        log.info("Calling public TokenResponse refresh(@RequestBody RefreshRequest request)");
        System.out.println("/refresh: " + refreshToken);
        if (refreshToken == null || refreshToken.isBlank()) {
           return ResponseEntity.status(401).build();
        }
        TokenResponse tokenResponse = authService.refresh(refreshToken);

        addRefreshCookie(response, tokenResponse.getRefreshToken());
        return ResponseEntity.ok(new TokenResponse(tokenResponse.getAccessToken()));
    }

    @PostMapping("/revoke")
    public ResponseEntity<Void> revoke(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        if (!refreshToken.isBlank()) {
            authService.revokeRefreshToken(refreshToken);
        }
        removeRefreshCooke(response);
        return ResponseEntity.noContent().build();
    }

    private void addRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    private void removeRefreshCooke(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
