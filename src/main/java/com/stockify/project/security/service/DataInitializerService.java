package com.stockify.project.security.service;

import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.model.entity.UserTenantMapping;
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
        createGurme();
    }

    private void createGurme() {
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setUsername("soner");
        userEntity1.setPassword(passwordEncoder.encode("test1234"));
        UserEntity saved1 = userRepository.save(userEntity1);


        UserEntity userEntity2 = new UserEntity();
        userEntity2.setUsername("selcuk");
        userEntity2.setPassword(passwordEncoder.encode("test1234"));
        UserEntity saved2 = userRepository.save(userEntity2);

    }
}
