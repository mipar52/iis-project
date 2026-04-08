package com.milan.iis_backend.module_graphql;

import com.milan.iis_backend.model.okta.dto.CreateOktaUserDto;
import com.milan.iis_backend.model.okta.dto.OktaUserDto;
import com.milan.iis_backend.model.okta.dto.UpdateOktaUserDto;
import com.milan.iis_backend.module_graphql.interfaces.UserGateway;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class OktaUserController {
    private final UserGateway userGateway;

    @GetMapping
    public List<OktaUserDto> getAll() {
        return userGateway.list();
    }

    @GetMapping("/{id}")
    public OktaUserDto getById(@PathVariable String id) {
        return userGateway.get(id);
    }

    @PostMapping
    public OktaUserDto create(@RequestBody CreateOktaUserDto createOktaUserDto) {
        return userGateway.create(createOktaUserDto);
    }

    @PutMapping("/{id}")
    public OktaUserDto update(@PathVariable String id, @RequestBody UpdateOktaUserDto updateOktaUserDto) {
        return userGateway.update(id, updateOktaUserDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        userGateway.delete(id);
    }
}
