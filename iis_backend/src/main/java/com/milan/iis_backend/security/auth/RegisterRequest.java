package com.milan.iis_backend.security.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    public String username;
    public String password;
    public String role;
}
