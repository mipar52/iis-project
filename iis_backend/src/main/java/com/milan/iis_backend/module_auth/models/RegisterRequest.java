package com.milan.iis_backend.module_auth.models;

import lombok.Data;

@Data
public class RegisterRequest {
    public String username;
    public String password;
    public String role;
}
