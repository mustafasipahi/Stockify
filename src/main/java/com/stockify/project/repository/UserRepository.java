package com.stockify.project.repository;

import com.stockify.project.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByFirstNameAndLastNameAndTenantId(String firstName, String lastName, Long tenantId);
}
