package com.project.envantra.repository;

import com.project.envantra.enums.UserStatus;
import com.project.envantra.model.entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameAndStatus(String username, UserStatus status);

    Optional<UserEntity> findByFirstNameAndLastName(String firstName, String lastName);
}
