package com.stockify.project.initialization;

import com.stockify.project.enums.Role;
import com.stockify.project.model.dto.UserDto;
import com.stockify.project.model.entity.CompanyInfoEntity;
import com.stockify.project.repository.CompanyInfoRepository;
import com.stockify.project.service.UserGetService;
import com.stockify.project.service.UserPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.constant.LoginConstant.*;
import static com.stockify.project.enums.TenantType.GURME;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializerService implements ApplicationRunner {

    private final UserGetService userGetService;
    private final UserPostService userPostService;
    private final CompanyInfoRepository companyInfoRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createGurme();
    }

    private void createGurme() {
        if (companyInfoRepository.findByTenantId(GURME.getTenantId()).isEmpty()) {
            CompanyInfoEntity companyInfo = CompanyInfoEntity.builder()
                    .companyName("Gurme Şirketler Grubu Lt.Ş.")
                    .companyAddress("Antalyada Bir Yerde Gülveren Tarafında")
                    .tenantId(GURME.getTenantId())
                    .build();
            companyInfoRepository.save(companyInfo);
        }
        if (userGetService.findByUsername(GURME_ADMIN_USER_NAME_1).isEmpty()) {
            UserDto userDto = UserDto.builder()
                    .username(GURME_ADMIN_USER_NAME_1)
                    .password(GURME_ADMIN_USER_PASSWORD_1)
                    .firstName("Soner")
                    .lastName("Sekanlı")
                    .email("sonersekanli@icloud.com")
                    .tenantId(GURME.getTenantId())
                    .role(Role.ROLE_ADMIN)
                    .build();
            userPostService.create(userDto);
        }
        if (userGetService.findByUsername(GURME_ADMIN_USER_NAME_2).isEmpty()) {
            UserDto userDto = UserDto.builder()
                    .username(GURME_ADMIN_USER_NAME_2)
                    .password(GURME_ADMIN_USER_PASSWORD_2)
                    .firstName("Selçuk")
                    .lastName("Yılmaz")
                    .email("selcuk.yilmaz.ant@gmail.com")
                    .tenantId(GURME.getTenantId())
                    .role(Role.ROLE_ADMIN)
                    .build();
            userPostService.create(userDto);
        }
    }
}
