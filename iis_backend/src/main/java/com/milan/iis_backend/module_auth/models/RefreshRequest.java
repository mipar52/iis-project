package com.milan.iis_backend.module_auth.models;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
