package com.project.envantra.service;

import com.project.envantra.converter.ProfileConverter;
import com.project.envantra.model.dto.CompanyDto;
import com.project.envantra.model.dto.ImageDto;
import com.project.envantra.model.dto.ProfileDto;
import com.project.envantra.model.dto.UserDto;
import com.project.envantra.service.document.DocumentGetService;
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
