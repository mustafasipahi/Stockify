package com.stockify.project.service;

import com.stockify.project.converter.CompanyConverter;
import com.stockify.project.exception.CompanyNotFoundException;
import com.stockify.project.model.dto.CompanyDto;
import com.stockify.project.model.entity.CompanyEntity;
import com.stockify.project.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.stockify.project.util.LoginContext.getUserId;

@Service
@RequiredArgsConstructor
public class CompanyGetService {

    private final CompanyRepository companyRepository;

    public CompanyEntity getCompany() {
        return companyRepository.findByCreatorUserId(getUserId())
                .orElseThrow(CompanyNotFoundException::new);
    }

    public CompanyDto getCompanyDetail() {
        CompanyEntity companyEntity = getCompany();
        return CompanyConverter.toDto(companyEntity);
    }
}
