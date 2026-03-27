package com.milan.iis_backend.okta;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.milan.iis_backend.model.okta.*;
import com.milan.iis_backend.model.okta.dto.CreateOktaUserDto;
import com.milan.iis_backend.model.okta.dto.OktaUserDto;
import com.milan.iis_backend.model.okta.dto.UpdateOktaUserDto;
import com.milan.iis_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "okta.api.mode", havingValue = "custom")
public class OktaUserCustomGateway implements UserGateway {
    private final UserRepository userRepository;


    @Override
    public List<OktaUserDto> list() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public OktaUserDto get(String id) {
        return userRepository.findById(id).stream().map(this::toDto)
                .findFirst()
                .orElse(null);
    }

    @Override
    public OktaUserDto create(CreateOktaUserDto createOktaUserDto) {
        OktaUser user = new OktaUser();
        user.setId(generateOktaId());
        user.setProfile(toOktaProfile(createOktaUserDto.getProfile()));
        OktaUser saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    public OktaUserDto update(String id, UpdateOktaUserDto updateOktaUserDto) {
        OktaUser user = userRepository.findById(id).orElseThrow();
        user.setLastUpdated(OffsetDateTime.now(ZoneOffset.UTC));

        OktaUser saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    public void delete(String id) {
        userRepository.deleteById(id);
    }

    private OktaUserDto toDto(OktaUser user) {
        OktaUserDto dto = new OktaUserDto();
        dto.setStatus(user.getStatus());
        dto.setCreated(user.getCreated());
        dto.setActivated(user.getActivated());
        dto.setStatusChanged(user.getStatusChanged());
        dto.setLastLogin(user.getLastLogin());
        dto.setType(user.getType());
        dto.setProfile(user.getProfile());
        dto.setCredentials(user.getCredentials());
        return dto;
    }

    private OktaUserProfile toOktaProfile(CreateOktaUserDto.Profile profile) {
        OktaUserProfile oktaUserProfile = new OktaUserProfile();
        oktaUserProfile.setFirstName(profile.getFirstName());
        oktaUserProfile.setLastName(profile.getLastName());
        oktaUserProfile.setEmail(profile.getEmail());
        oktaUserProfile.setLogin(profile.getLogin());
        oktaUserProfile.setMobilePhone(profile.getMobilePhone());
        return oktaUserProfile;
    }

    private static String generateOktaId() {
        return "00u" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
    }
}
