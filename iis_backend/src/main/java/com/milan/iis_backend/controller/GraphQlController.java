package com.milan.iis_backend.controller;

import com.milan.iis_backend.model.okta.OktaUser;
import com.milan.iis_backend.model.okta.OktaUserProfile;
import com.milan.iis_backend.model.okta.OktaUserType;
import com.milan.iis_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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

    @QueryMapping
    public List<OktaUser> users() {
        return userRepository.findAll();
    }

    @QueryMapping
    public OktaUser user(@Argument("id") String id) {
        return userRepository.findById(id).orElse(null);
    }

    @MutationMapping
    public OktaUser createUser(@Argument("input") CreateUserInput createUserInput) {
        OktaUser oktaUser = new OktaUser();
        oktaUser.setId(generateOktaLikeId());

        oktaUser.setStatus(createUserInput.status != null ? createUserInput.status() : "ACTIVE");
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        oktaUser.setCreated(now);
        oktaUser.setActivated(now);
        oktaUser.setLastUpdated(now);
        oktaUser.setStatusChanged(now);

        OktaUserProfile oktaUserProfile = new OktaUserProfile();

        oktaUserProfile.setFirstName(createUserInput.profile().firstName());
        oktaUserProfile.setLastName(createUserInput.profile().lastName());
        oktaUserProfile.setLogin(createUserInput.profile().login());
        oktaUserProfile.setMobilePhone(createUserInput.profile().mobilePhone());
        oktaUserProfile.setEmail(createUserInput.profile().email());
        oktaUserProfile.setSecondEmail(createUserInput.profile().secondEmail());

        oktaUser.setProfile(oktaUserProfile);

        if (createUserInput.type() != null && createUserInput.type().id() != null) {
            OktaUserType oktaUserType = new OktaUserType();
            oktaUserType.setId(createUserInput.type().id());
            oktaUser.setType(oktaUserType);
        }
        return userRepository.save(oktaUser);
    }

    @MutationMapping
    public OktaUser updateUser(@Argument String id, @Argument("input") UpdateUserInput updateUserInput) {
        OktaUser oktaUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found!"));

        if (updateUserInput.status() != null) {
            oktaUser.setStatus(updateUserInput.status());
            oktaUser.setStatusChanged(OffsetDateTime.now(ZoneOffset.UTC));
        }

        if (updateUserInput.profile() != null) {
            if (oktaUser.getProfile() == null) oktaUser.setProfile(new OktaUserProfile());
            OktaUserProfile profile = oktaUser.getProfile();

            if (updateUserInput.profile().firstName() != null) profile.setFirstName(updateUserInput.profile().firstName());
            if (updateUserInput.profile().lastName() != null) profile.setLastName(updateUserInput.profile().lastName());
            if (updateUserInput.profile().login() != null) profile.setLogin(updateUserInput.profile().login());
            if (updateUserInput.profile().email() != null) profile.setEmail(updateUserInput.profile().email());
            if (updateUserInput.profile().mobilePhone() != null) profile.setMobilePhone(updateUserInput.profile().mobilePhone());
            if (updateUserInput.profile().secondEmail() != null) profile.setSecondEmail(updateUserInput.profile().secondEmail());
        }

        if (updateUserInput.type() != null) {
            if (oktaUser.getType() == null) oktaUser.setType(new OktaUserType());
            oktaUser.getType().setId(updateUserInput.type().id());
        }

        oktaUser.setLastUpdated(OffsetDateTime.now(ZoneOffset.UTC));

        return userRepository.save(oktaUser);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument("id") String id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

    private static String generateOktaLikeId() {
        return "00u" + UUID.randomUUID().toString().replace("-", "").substring(1, 17);
    }

    public record CreateUserInput(CreateProfileInput profile, TypeInput type, String status) {}
    public record UpdateUserInput(UpdateProfileInput profile, TypeInput type, String status) {}
    public record CreateProfileInput(String firstName, String lastName, String login, String email, String mobilePhone, String secondEmail) {}
    public record UpdateProfileInput(String firstName, String lastName, String login, String email, String mobilePhone, String secondEmail) {}
    public record TypeInput(String id) {}
}
