package com.stockify.project.security.service;

import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataInitializerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initData() {
        if (userRepository.count() == 0) {
            UserEntity defaultUserEntity = new UserEntity();
            defaultUserEntity.setUsername("soner");
            defaultUserEntity.setPassword(passwordEncoder.encode("test1234"));
            userRepository.save(defaultUserEntity);
        }
    }
}
