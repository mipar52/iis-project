package com.milan.iis_backend.module_auth.models;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
