package com.stockify.project.converter;

import com.stockify.project.model.dto.CompanyDto;
import com.stockify.project.model.entity.CompanyEntity;
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
                .companyName(companyEntity.getCompanyName())
                .companyAddress(companyEntity.getCompanyAddress())
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
