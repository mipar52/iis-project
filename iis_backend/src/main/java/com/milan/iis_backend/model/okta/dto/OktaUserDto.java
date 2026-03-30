package com.milan.iis_backend.model.okta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milan.iis_backend.model.okta.OktaLinks;
import com.milan.iis_backend.model.okta.OktaUserCredentials;
import com.milan.iis_backend.model.okta.OktaUserProfile;
import lombok.Data;

@Data
public class OktaUserDto {
    private String id;
    private String status;

    private OktaUserProfile profile;
    private OktaUserCredentials credentials;

    @JsonProperty("_links")
    private OktaLinks links;
}
