package com.stockify.project.initialization;

import com.stockify.project.model.entity.CompanyInfoEntity;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.repository.CompanyInfoRepository;
import com.stockify.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.constant.LoginConstant.*;
import static com.stockify.project.enums.TenantType.GURME;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializerService implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CompanyInfoRepository companyInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createGurme();
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
            user.setEmail("");
            userRepository.save(user);
        }
        if (userRepository.findByUsername(GURME_ADMIN_USER_NAME_2).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername(GURME_ADMIN_USER_NAME_2);
            user.setPassword(passwordEncoder.encode(GURME_ADMIN_USER_PASSWORD_2));
            user.setTenantId(GURME.getTenantId());
            user.setEmail("");
            userRepository.save(user);
        }
    }
}
