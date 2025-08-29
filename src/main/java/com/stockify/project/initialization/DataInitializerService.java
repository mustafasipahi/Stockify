package com.stockify.project.initialization;

import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static com.stockify.project.constant.LoginConstant.*;
import static com.stockify.project.enums.TenantType.GURME;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.database.initialize", havingValue = "true")
public class DataInitializerService implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initializeTestData();
        createGurme();
    }

    private void initializeTestData() {
        try {
            Resource resource = new ClassPathResource("initialization.sql");
            ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
        } catch (Exception e) {
            throw new RuntimeException("Database initialization failed", e);
        }
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
