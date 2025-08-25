package com.stockify.project.initialization;

import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.constant.LoginConstant.*;
import static com.stockify.project.enums.TenantType.GURME;

@Component
@RequiredArgsConstructor
public class DataInitializerService implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createGurme();
    }

    private void createGurme() {
        if (userRepository.findByUsername(GURME_ADMIN_USER_NAME_1).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername(GURME_ADMIN_USER_NAME_1);
            user.setPassword(passwordEncoder.encode(GURME_ADMIN_USER_PASSWORD_1));
            user.setTenantId(GURME.getTenantId());
            userRepository.save(user);
        }

        if (userRepository.findByUsername(GURME_ADMIN_USER_NAME_2).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername(GURME_ADMIN_USER_NAME_2);
            user.setPassword(passwordEncoder.encode(GURME_ADMIN_USER_PASSWORD_2));
            user.setTenantId(GURME.getTenantId());
            userRepository.save(user);
        }
    }
}
