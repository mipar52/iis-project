package com.milan.iis_backend.model.okta;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OktaUserCredentials {
    private OktaPassword password = new OktaPassword();

    private OktaProvider provider = new OktaProvider("OKTA", "OKTA");
}
