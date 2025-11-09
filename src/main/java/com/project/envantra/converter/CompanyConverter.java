package com.project.envantra.converter;

import com.project.envantra.model.dto.CompanyDto;
import com.project.envantra.model.entity.CompanyEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyConverter {

    public static CompanyEntity toEntity(Long creatorUserId) {
        return CompanyEntity.builder()
                .creatorUserId(creatorUserId)
                .build();
    }

    public static CompanyDto toDto(CompanyEntity companyEntity) {
        return CompanyDto.builder()
                .logoImageId(companyEntity.getLogoImageId())
                .creatorUserId(companyEntity.getCreatorUserId())
                .name(companyEntity.getName())
                .address(companyEntity.getAddress())
                .phoneNumber(companyEntity.getPhoneNumber())
                .invoiceUsername(companyEntity.getInvoiceUsername())
                .invoicePassword(companyEntity.getInvoicePassword())
                .build();
    }

    public static CompanyDto toIdDto(CompanyEntity companyEntity) {
        return CompanyDto.builder()
                .companyId(companyEntity.getId())
                .build();
    }
}
