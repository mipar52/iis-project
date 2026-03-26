package com.milan.iis_backend.model.okta;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "okta_users")
public class OktaUser {
    @Id
    @Column(length = 64)
    private String id;

    private String status;

    private Instant created;
    private Instant activated;
    private Instant statusChanged;
    private Instant lastLogin;
    private Instant lastUpdated;
    private Instant passwordChanged;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "type_id"))
    private OktaUserType type;

    @Embedded
    private OktaUserProfile profile;

    @Embedded
    private OktaUserCredentials credentials;

    @Transient
    @JsonProperty("_links")
    private OktaLinks links;

    @PostLoad
    @PostPersist
    @PostUpdate
    private void buildLinks() {
        // href u custom modu može biti link na tvoj backend
        this.links = OktaLinks.self("http://localhost:8081/api/v1/users/" + this.id);
    }
}
