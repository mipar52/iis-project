package com.milan.iis_backend.service.implementation;

import com.milan.iis_backend.model.okta.OktaUser;
import com.milan.iis_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OktaUserService {
    private final UserRepository userRepository;

    public List<OktaUser> findAll() {
        return userRepository.findAll();
    }
}
