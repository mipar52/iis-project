package com.milan.iis_backend.model;

import com.milan.iis_backend.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class OktaUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String login;

    private String mobilePhone;

    @Column(nullable = false)
    private String sourceType; // "XML" ili "JSON"

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
