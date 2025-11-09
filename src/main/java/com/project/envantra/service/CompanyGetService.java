package com.project.envantra.service;

import com.project.envantra.converter.CompanyConverter;
import com.project.envantra.exception.CompanyNotFoundException;
import com.project.envantra.model.dto.CompanyDto;
import com.project.envantra.model.entity.CompanyEntity;
import com.project.envantra.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.project.envantra.util.LoginContext.getUserId;

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
