package com.milan.iis_backend.model.okta.dto;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class UpdateOktaUserDto {
    @Valid
    private Profile profile;

    private String status;

    private Type type;

    @Data
    public static class Type {
        private String id;
    }
}
