package com.milan.iis_backend.utils;

import com.milan.iis_backend.model.okta.OktaUser;
import com.milan.iis_backend.model.okta.OktaUserProfile;
import com.milan.iis_backend.model.okta.dto.OktaUserDto;
import com.milan.iis_backend.model.okta.dto.json.OktaUserJson;
import com.milan.iis_backend.model.okta.dto.xml.OktaUserXml;

import java.time.OffsetDateTime;
import java.util.UUID;

public class OktaUtils {
    public static String generateOktaId() {
        return "00u" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
    }

    public static OktaUser toOktaUser(OktaUserDto dto, OffsetDateTime time) {
        OktaUser user = new OktaUser();
        user.setId(OktaUtils.generateOktaId());
        user.setStatus(dto.getStatus());
        user.setProfile(dto.getProfile());

        user.setCreated(time);
        user.setActivated(time);
        user.setStatusChanged(time);
        user.setLastLogin(time);
        user.setLastUpdated(time);
        user.setPasswordChanged(time);


        return user;
    }

    public static OktaUser toOktaUserFromXml(OktaUserXml xml, OffsetDateTime time) {
        OktaUser user = new OktaUser();
        user.setId(OktaUtils.generateOktaId());
        user.setStatus(xml.status);
        OktaUserProfile profile = new OktaUserProfile();
        profile.setFirstName(xml.profile.firstName);
        profile.setLastName(xml.profile.lastName);
        profile.setEmail(xml.profile.email);
        profile.setLogin(xml.profile.login);
        profile.setMobilePhone(xml.profile.mobilePhone);
        user.setProfile(profile);

        user.setCreated(time);
        user.setActivated(time);
        user.setStatusChanged(time);
        user.setLastLogin(time);
        user.setLastUpdated(time);
        user.setPasswordChanged(time);

        return user;
    }

    public static OktaUser toOktaUserFromJson(OktaUserJson json, OffsetDateTime time) {
        OktaUser user = new OktaUser();
        user.setId(OktaUtils.generateOktaId());
        user.setStatus(json.status);
        OktaUserProfile profile = new OktaUserProfile();
        profile.setFirstName(json.profile.firstName);
        profile.setLastName(json.profile.lastName);
        profile.setEmail(json.profile.email);
        profile.setLogin(json.profile.login);
        profile.setMobilePhone(json.profile.mobilePhone);
        user.setProfile(profile);

        user.setCreated(time);
        user.setActivated(time);
        user.setStatusChanged(time);
        user.setLastLogin(time);
        user.setLastUpdated(time);
        user.setPasswordChanged(time);

        return user;
    }

    public static OktaUserDto toDto(OktaUser user) {
        OktaUserDto dto = new OktaUserDto();
        dto.setStatus(user.getStatus());
        dto.setProfile(user.getProfile());
        dto.setCredentials(user.getCredentials());
        return dto;
    }
}
