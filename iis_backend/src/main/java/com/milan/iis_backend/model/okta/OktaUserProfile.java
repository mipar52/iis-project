package com.milan.iis_backend.model.okta;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class OktaUserProfile {
    private String firstName;
    private String lastName;

    private String mobilePhone;
    private String secondEmail;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String email;

}
