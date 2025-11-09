package com.stockify.project.converter;

import com.stockify.project.model.dto.CompanyDto;
import com.stockify.project.model.dto.ImageDto;
import com.stockify.project.model.dto.ProfileDto;
import com.stockify.project.model.dto.UserDto;
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
