package com.stockify.project.service;

import com.stockify.project.exception.CompanyInfoNotFoundException;
import com.stockify.project.model.dto.ReceiptInfoDto;
import com.stockify.project.model.entity.CompanyInfoEntity;
import com.stockify.project.repository.CompanyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.stockify.project.constant.CacheConstants.COMPANY_DETAIL;

@Service
@RequiredArgsConstructor
public class ReceiptInfoService {

    private final CompanyInfoRepository companyInfoRepository;

    @Cacheable(value = COMPANY_DETAIL, key = "#tenantId")
    public ReceiptInfoDto getReceiptInfo(Long tenantId) {
        CompanyInfoEntity companyInfoEntity = companyInfoRepository.findByTenantId(tenantId)
                .orElseThrow(CompanyInfoNotFoundException::new);
        return ReceiptInfoDto.builder()
                .companyName(companyInfoEntity.getCompanyName())
                .companyAddress(companyInfoEntity.getCompanyAddress())
                .cari(companyInfoEntity.getCari())
                .build();
    }
}
