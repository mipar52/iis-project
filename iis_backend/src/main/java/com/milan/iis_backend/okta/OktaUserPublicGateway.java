package com.milan.iis_backend.okta;

import com.milan.iis_backend.model.okta.dto.CreateOktaUserDto;
import com.milan.iis_backend.model.okta.dto.OktaUserDto;
import com.milan.iis_backend.model.okta.dto.UpdateOktaUserDto;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name="okta.api.mode", havingValue="public", matchIfMissing=true)
public class OktaUserPublicGateway implements  UserGateway {

    private final RestClient oktaClient;

    @Override
    public List<OktaUserDto> list() {
        OktaUserDto[] array = oktaClient.get()
                .uri("/api/v1/users")
                .retrieve()
                .body(OktaUserDto[].class);
        return array == null ? List.of() : Arrays.asList(array);
    }

    @Override
    public OktaUserDto get(String id) {
        return oktaClient.get()
                .uri("/api/v1/users/{id}", id)
                .retrieve()
                .body(OktaUserDto.class);
    }

    @Override
    public OktaUserDto create(CreateOktaUserDto createOktaUserDto) {
        return oktaClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createOktaUserDto)
                .retrieve()
                .body(OktaUserDto.class);
    }

    @Override
    public OktaUserDto update(String id, UpdateOktaUserDto updateOktaUserDto) {
        return oktaClient.put()
                .uri("/api/v1/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateOktaUserDto)
                .retrieve()
                .body(OktaUserDto.class);
    }

    @Override
    public void delete(String id) {
        oktaClient.delete()
                .uri("/api/v1/users/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
