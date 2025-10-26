package com.stockify.project.service;

import com.stockify.project.exception.UserNotFoundException;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.stockify.project.util.TenantContext.getTenantId;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGetService {

    private final UserRepository userRepository;

    public UserEntity findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> findByFirstNameAndLastNameAndTenantId(String firstName, String lastName) {
        return userRepository.findByFirstNameAndLastNameAndTenantId(firstName, lastName, getTenantId());
    }
}
