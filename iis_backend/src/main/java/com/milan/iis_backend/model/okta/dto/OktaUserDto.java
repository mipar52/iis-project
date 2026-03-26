package com.milan.iis_backend.model.okta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milan.iis_backend.model.okta.OktaLinks;
import com.milan.iis_backend.model.okta.OktaUserCredentials;
import com.milan.iis_backend.model.okta.OktaUserProfile;
import com.milan.iis_backend.model.okta.OktaUserType;
import lombok.Data;

import java.time.Instant;

@Data
public class OktaUserDto {
    private String id;
    private String status;
    private Instant created;
    private Instant activated;
    private Instant statusChanged;
    private Instant lastLogin;
    private Instant lastUpdated;
    private Instant passwordChanged;

    private OktaUserType type;
    private OktaUserProfile profile;
    private OktaUserCredentials credentials;

    @JsonProperty("_links")
    private OktaLinks links;
}
