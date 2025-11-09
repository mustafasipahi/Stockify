package com.project.envantra.service;

import com.project.envantra.converter.CompanyConverter;
import com.project.envantra.exception.CompanyNotFoundException;
import com.project.envantra.model.dto.CompanyDto;
import com.project.envantra.model.entity.CompanyEntity;
import com.project.envantra.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    public boolean hasInvoiceInfo() {
        CompanyEntity companyEntity = getCompany();
        boolean hasValidUsername = StringUtils.isNotBlank(companyEntity.getInvoiceUsername());
        boolean hasValidPassword = StringUtils.isNotBlank(companyEntity.getInvoicePassword());
        return hasValidUsername && hasValidPassword;
    }
}
