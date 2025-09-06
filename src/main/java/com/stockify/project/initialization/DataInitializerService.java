package com.stockify.project.initialization;

import com.stockify.project.model.entity.CompanyInfoEntity;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.repository.CompanyInfoRepository;
import com.stockify.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static com.stockify.project.constant.LoginConstant.*;
import static com.stockify.project.enums.TenantType.GURME;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializerService implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CompanyInfoRepository companyInfoRepository;
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
            //Resource resource = new ClassPathResource("/initialization/initialization.sql");
            //ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
        } catch (Exception e) {
            log.error("Initialize Test Data Error!", e);
        }
    }

    private void createGurme() {
        if (companyInfoRepository.findByTenantId(GURME.getTenantId()).isEmpty()) {
            CompanyInfoEntity companyInfoEntity = new CompanyInfoEntity();
            companyInfoEntity.setCompanyName("Gurme Şirketler Grubu Lt.Ş.");
            companyInfoEntity.setCompanyAddress("Antalyada Bir Yerde Gülveren Tarafında");
            companyInfoEntity.setTenantId(GURME.getTenantId());
            companyInfoRepository.save(companyInfoEntity);
        }
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
