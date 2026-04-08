package com.milan.iis_backend.module_auth.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
