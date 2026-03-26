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
    public static class Profile {
        @NotBlank private String firstName;
        @NotBlank
        private String lastName;

        @NotBlank private String login;

        @NotBlank @Email
        private String email;

        private String mobilePhone;
        private String secondEmail;

    }

    @Data
    public static class Type {
        private String id;
        // getters/setters
    }
}
