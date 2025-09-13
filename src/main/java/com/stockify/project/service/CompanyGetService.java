package com.stockify.project.service;

import com.stockify.project.exception.CompanyInfoNotFoundException;
import com.stockify.project.model.dto.CompanyInfoDto;
import com.stockify.project.model.entity.CompanyInfoEntity;
import com.stockify.project.repository.CompanyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class CompanyGetService {

    private final CompanyInfoRepository companyInfoRepository;

    public CompanyInfoDto getCompanyInfo() {
        CompanyInfoEntity companyInfoEntity = companyInfoRepository.findByTenantId(getTenantId())
                .orElseThrow(CompanyInfoNotFoundException::new);
        return CompanyInfoDto.builder()
                .companyName(companyInfoEntity.getCompanyName())
                .companyAddress(companyInfoEntity.getCompanyAddress())
                .build();
    }
}
