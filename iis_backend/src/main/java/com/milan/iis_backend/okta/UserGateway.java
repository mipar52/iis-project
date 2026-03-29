package com.milan.iis_backend.okta;

import com.milan.iis_backend.controller.GraphQlController;
import com.milan.iis_backend.model.okta.dto.CreateOktaUserDto;
import com.milan.iis_backend.model.okta.dto.OktaUserDto;
import com.milan.iis_backend.model.okta.dto.UpdateOktaUserDto;

import java.util.List;

public interface UserGateway {
    List<OktaUserDto> list();
    OktaUserDto get(String id);
    OktaUserDto create(CreateOktaUserDto createUserInput);
    OktaUserDto update(String id, UpdateOktaUserDto updateOktaUserDto);
    void delete(String id);
}
