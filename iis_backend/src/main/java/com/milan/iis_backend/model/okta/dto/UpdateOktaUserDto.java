package com.milan.iis_backend.model.okta.dto;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class UpdateOktaUserDto {
    @Valid
    private Profile profile;

    private String status; // optional (npr ACTIVE/SUSPENDED) ako želiš

    private Type type; // optional

    @Data
    public static class Profile {
        private String firstName;
        private String lastName;
        private String login;
        private String email;
        private String mobilePhone;
        private String secondEmail;
        // getters/setters
    }

    @Data
    public static class Type {
        private String id;
    }
}
