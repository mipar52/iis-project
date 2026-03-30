package com.milan.iis_backend.model.okta.dto;

import lombok.Data;

@Data
public class Profile {
    private String firstName;
    private String lastName;
    private String login;
    private String email;
    private String mobilePhone;
    private String secondEmail;
}
