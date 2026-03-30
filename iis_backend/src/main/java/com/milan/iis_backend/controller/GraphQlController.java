package com.milan.iis_backend.controller;

import com.milan.iis_backend.model.okta.OktaUser;
import com.milan.iis_backend.model.okta.OktaUserProfile;
import com.milan.iis_backend.model.okta.OktaUserType;
import com.milan.iis_backend.model.okta.dto.CreateOktaUserDto;
import com.milan.iis_backend.model.okta.dto.OktaUserDto;
import com.milan.iis_backend.model.okta.dto.UpdateOktaUserDto;
import com.milan.iis_backend.okta.UserGateway;
import com.milan.iis_backend.repository.UserRepository;
import com.milan.iis_backend.utils.OktaUtils;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class GraphQlController {
    private final UserRepository userRepository;
    private final UserGateway userGateway;
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @QueryMapping
    public List<OktaUserDto> users() {
        return userGateway.list();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @QueryMapping
    public OktaUserDto user(@Argument("id") String id) {
        return userGateway.get(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public OktaUserDto createUser(@Argument("input") CreateOktaUserDto createUserInput) {
        return userGateway.create(createUserInput);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public OktaUserDto updateUser(@Argument String id, @Argument("input") UpdateOktaUserDto updateOktaUserDto) {
        return userGateway.update(id, updateOktaUserDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Boolean deleteUser(@Argument("id") String id) {
        userGateway.delete(id);
        return true;
    }

    public record TypeInput(String id) {}
}
