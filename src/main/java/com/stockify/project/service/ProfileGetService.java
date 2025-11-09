package com.stockify.project.service;

import com.stockify.project.converter.ProfileConverter;
import com.stockify.project.model.dto.CompanyDto;
import com.stockify.project.model.dto.ImageDto;
import com.stockify.project.model.dto.ProfileDto;
import com.stockify.project.model.dto.UserDto;
import com.stockify.project.service.document.DocumentGetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileGetService {

    private final UserGetService userGetService;
    private final CompanyGetService companyGetService;
    private final DocumentGetService documentGetService;

    public ProfileDto detail() {
        UserDto user = userGetService.getLoginUserDetail();
        CompanyDto company = companyGetService.getCompanyDetail();
        ImageDto images = documentGetService.getImages(user.getProfileImageId(), company.getLogoImageId());
        return ProfileConverter.toDto(user, company, images);
    }
}
