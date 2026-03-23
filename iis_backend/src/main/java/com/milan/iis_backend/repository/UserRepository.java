package com.milan.iis_backend.repository;

import com.milan.iis_backend.model.OktaUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<OktaUser, Long> {}
