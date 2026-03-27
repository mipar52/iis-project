package com.milan.iis_backend.model.okta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milan.iis_backend.model.okta.OktaLinks;
import com.milan.iis_backend.model.okta.OktaUserCredentials;
import com.milan.iis_backend.model.okta.OktaUserProfile;
import com.milan.iis_backend.model.okta.OktaUserType;
import lombok.Data;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
public class OktaUserDto {
    private String id;
    private String status;
    private OffsetDateTime created;
    private OffsetDateTime activated;
    private OffsetDateTime statusChanged;
    private OffsetDateTime lastLogin;
    private OffsetDateTime lastUpdated;
    private OffsetDateTime passwordChanged;

    private OktaUserType type;
    private OktaUserProfile profile;
    private OktaUserCredentials credentials;

    @JsonProperty("_links")
    private OktaLinks links;
}
