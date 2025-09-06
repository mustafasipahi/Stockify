package com.stockify.project.service;

import com.stockify.project.exception.CompanyInfoNotFoundException;
import com.stockify.project.model.dto.CompanyInfoDto;
import com.stockify.project.model.entity.CompanyInfoEntity;
import com.stockify.project.repository.CompanyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyGetService {

    private final CompanyInfoRepository companyInfoRepository;

    public CompanyInfoDto getCompanyInfo(Long tenantId) {
        CompanyInfoEntity companyInfoEntity = companyInfoRepository.findByTenantId(tenantId)
                .orElseThrow(CompanyInfoNotFoundException::new);
        return CompanyInfoDto.builder()
                .companyName(companyInfoEntity.getCompanyName())
                .companyAddress(companyInfoEntity.getCompanyAddress())
                .build();
    }
}
