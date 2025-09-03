package com.stockify.project.service;

import com.stockify.project.exception.CompanyInfoNotFoundException;
import com.stockify.project.model.dto.CompanyInfoDto;
import com.stockify.project.model.entity.CompanyInfoEntity;
import com.stockify.project.repository.CompanyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.stockify.project.constant.CacheConstants.COMPANY_DETAIL;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyInfoRepository companyInfoRepository;

    @Cacheable(value = COMPANY_DETAIL, key = "#tenantId")
    public CompanyInfoDto getReceiptInfo(Long tenantId) {
        CompanyInfoEntity companyInfoEntity = companyInfoRepository.findByTenantId(tenantId)
                .orElseThrow(CompanyInfoNotFoundException::new);
        return CompanyInfoDto.builder()
                .companyName(companyInfoEntity.getCompanyName())
                .companyAddress(companyInfoEntity.getCompanyAddress())
                .cari(companyInfoEntity.getCari())
                .build();
    }
}
