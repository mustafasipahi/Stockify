package com.project.envantra.service;

import com.project.envantra.converter.CompanyConverter;
import com.project.envantra.model.dto.CompanyDto;
import com.project.envantra.model.entity.CompanyEntity;
import com.project.envantra.repository.CompanyRepository;
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
