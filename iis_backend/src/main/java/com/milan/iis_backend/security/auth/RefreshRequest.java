package com.milan.iis_backend.security.auth;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
