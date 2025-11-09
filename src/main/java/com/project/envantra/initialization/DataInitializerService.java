package com.project.envantra.initialization;

import com.project.envantra.enums.Role;
import com.project.envantra.model.dto.UserDto;
import com.project.envantra.model.entity.CompanyEntity;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.service.CompanyPostService;
import com.project.envantra.service.UserPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializerService implements ApplicationRunner {

    private final UserPostService userPostService;
    private final CompanyPostService companyPostService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        //createTest();
        //createGurme();
    }

    private void createTest() {
        UserDto userDto1 = UserDto.builder()
                .username("test1")
                .password("test1234")
                .firstName("Test1")
                .lastName("User1")
                .email("test1@user.com")
                .role(Role.ROLE_ADMIN)
                .build();
        UserEntity userEntity1 = userPostService.save(userDto1);
        CompanyEntity company1 = CompanyEntity.builder()
                .creatorUserId(userEntity1.getId())
                .name("Test1 Şirketler Grubu Lt.Ş.")
                .address("Antalyada Bir Yerde Test1")
                .invoiceUsername("mehmetali@birhesap.com.tr")
                .invoicePassword("Abc123456!")
                .build();
        companyPostService.save(company1);

        UserDto userDto2 = UserDto.builder()
                .username("test2")
                .password("test4321")
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

    private void createGurme() {
        UserDto userDto1 = UserDto.builder()
                .username("soner")
                .password("test1234")
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

        UserDto userDto2 = UserDto.builder()
                .username("selcuk")
                .password("test4321")
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
