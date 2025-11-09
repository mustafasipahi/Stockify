package com.stockify.project.service;

import com.stockify.project.converter.CompanyConverter;
import com.stockify.project.model.dto.CompanyDto;
import com.stockify.project.model.entity.CompanyEntity;
import com.stockify.project.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyPostService {

    private final CompanyRepository companyRepository;

    public CompanyDto save(CompanyEntity companyEntity) {
        CompanyEntity savedCompany = companyRepository.save(companyEntity);
        return CompanyConverter.toIdDto(savedCompany);
    }

    public void saveBrokerCompany(Long creatorUserId) {
        CompanyEntity companyEntity = CompanyConverter.toEntity(creatorUserId);
        companyRepository.save(companyEntity);
    }
}
