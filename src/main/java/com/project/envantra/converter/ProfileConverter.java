package com.project.envantra.converter;

import com.project.envantra.model.dto.CompanyDto;
import com.project.envantra.model.dto.ImageDto;
import com.project.envantra.model.dto.ProfileDto;
import com.project.envantra.model.dto.UserDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileConverter {

    public static ProfileDto toDto(UserDto user, CompanyDto company, ImageDto images) {
        return ProfileDto.builder()
                .user(user)
                .company(company)
                .images(images)
                .build();
    }
}
