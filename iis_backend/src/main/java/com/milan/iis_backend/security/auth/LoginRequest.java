package com.milan.iis_backend.security.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
