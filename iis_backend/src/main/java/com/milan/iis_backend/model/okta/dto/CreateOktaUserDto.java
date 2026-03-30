package com.milan.iis_backend.model.okta.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOktaUserDto {
    @Valid
    private Profile profile;

    private Type type; // optional

    @Data
    public static class Type {
        private String id;
        // getters/setters
    }
}
