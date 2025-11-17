package com.project.envantra.initialization;

import com.project.envantra.enums.Role;
import com.project.envantra.enums.UserStatus;
import com.project.envantra.model.dto.UserDto;
import com.project.envantra.model.entity.CompanyEntity;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.service.CompanyPostService;
import com.project.envantra.service.UserGetService;
import com.project.envantra.service.UserPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializerService implements ApplicationRunner {

    @Value("${initialize-config.value}")
    private String initializeValue;

    private final UserGetService userGetService;
    private final UserPostService userPostService;
    private final CompanyPostService companyPostService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (StringUtils.isBlank(initializeValue)) {
            return;
        }
        if ("GURME".equals(initializeValue)) {
            createTest();
        }
        if ("TEST".equals(initializeValue)) {
            createGurme();
        }
    }

    private void createTest() {
        String username1 = "test";
        Optional<UserEntity> test1 = userGetService.findByUsername(username1);
        if (test1.isEmpty()) {
            UserDto userDto1 = UserDto.builder()
                    .username(username1)
                    .password("test1234")
                    .status(UserStatus.ACTIVE)
                    .firstName("Apple")
                    .lastName("Test")
                    .email("test@apple.com")
                    .role(Role.ROLE_ADMIN)
                    .build();
            UserEntity userEntity1 = userPostService.save(userDto1);
            CompanyEntity company1 = CompanyEntity.builder()
                    .creatorUserId(userEntity1.getId())
                    .name("Test Apple Lt.Ş.")
                    .address("Amerika")
                    .build();
            companyPostService.save(company1);
        }

        String username2 = "test2";
        Optional<UserEntity> test2 = userGetService.findByUsername(username2);
        if (test2.isEmpty()) {
            UserDto userDto2 = UserDto.builder()
                    .username(username2)
                    .password("test4321")
                    .status(UserStatus.ACTIVE)
                    .firstName("Test2")
                    .lastName("User2")
                    .email("test2@user.com")
                    .role(Role.ROLE_ADMIN)
                    .build();
            UserEntity userEntity2 = userPostService.save(userDto2);
            CompanyEntity company2 = CompanyEntity.builder()
                    .creatorUserId(userEntity2.getId())
                    .name("Test2 Şirketler Grubu Lt.Ş.")
                    .address("Antalyada Bir Yerde Test2")
                    .invoiceUsername("mehmetali@birhesap.com.tr")
                    .invoicePassword("Abc123456!")
                    .build();
            companyPostService.save(company2);
        }
    }

    private void createGurme() {
        String username1 = "soner";
        Optional<UserEntity> test1 = userGetService.findByUsername(username1);
        if (test1.isEmpty()) {
            UserDto userDto1 = UserDto.builder()
                    .username(username1)
                    .password("test1234")
                    .status(UserStatus.ACTIVE)
                    .firstName("Soner")
                    .lastName("Sekanlı")
                    .email("sonersekanli@icloud.com")
                    .role(Role.ROLE_ADMIN)
                    .build();
            UserEntity userEntity1 = userPostService.save(userDto1);
            CompanyEntity company1 = CompanyEntity.builder()
                    .creatorUserId(userEntity1.getId())
                    .name("Gurme Şirketler Grubu Lt.Ş.")
                    .address("Antalyada Bir Yerde Gülveren Tarafında")
                    .build();
            companyPostService.save(company1);
        }

        String username2 = "selcuk";
        Optional<UserEntity> test2 = userGetService.findByUsername(username2);
        if (test2.isEmpty()) {
            UserDto userDto2 = UserDto.builder()
                    .username("selcuk")
                    .password("test4321")
                    .status(UserStatus.ACTIVE)
                    .firstName("Selçuk")
                    .lastName("Yılmaz")
                    .email("selcuk.yilmaz.ant@gmail.com")
                    .role(Role.ROLE_ADMIN)
                    .build();
            UserEntity userEntity2 = userPostService.save(userDto2);
            CompanyEntity company2 = CompanyEntity.builder()
                    .creatorUserId(userEntity2.getId())
                    .name("Gurme Şirketler Grubu Lt.Ş.")
                    .address("Antalyada Bir Yerde Gülveren Tarafında")
                    .build();
            companyPostService.save(company2);
        }
    }
}
