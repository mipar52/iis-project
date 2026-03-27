package com.milan.iis_backend.model.okta;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class OktaUserType {
    private String id;
}
