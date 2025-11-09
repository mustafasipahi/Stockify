package com.stockify.project.service;

import com.stockify.project.exception.UsernameAlreadyUsedException;
import com.stockify.project.model.entity.CompanyEntity;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.model.request.ProfileCompanyUpdateRequest;
import com.stockify.project.model.request.ProfileUpdateRequest;
import com.stockify.project.model.request.ProfileUserUpdateRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.service.document.DocumentPostService;
import com.stockify.project.validator.ImageUploadValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.stockify.project.util.LoginContext.getUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilePostService {

    private final UserGetService userGetService;
    private final UserPostService userPostService;
    private final CompanyGetService companyGetService;
    private final CompanyPostService companyPostService;
    private final PasswordEncoder passwordEncoder;
    private final DocumentPostService documentPostService;

    @Transactional
    public void update(ProfileUpdateRequest request) {
        UserEntity userEntity = getUser();
        if (request.getUser() != null) {
            updateUserDetail(request.getUser(), userEntity);
        }
        CompanyEntity companyEntity = companyGetService.getCompany();
        if (request.getCompany() != null) {
            updateCompanyDetail(request.getCompany(), companyEntity);
        }
        userPostService.save(userEntity);
        companyPostService.save(companyEntity);
    }

    @Transactional
    public void uploadProfileImage(MultipartFile file) {
        ImageUploadValidator.validateImage(file);
        DocumentResponse documentResponse = documentPostService.uploadProfileImage(file);
        UserEntity userEntity = getUser();
        userEntity.setProfileImageId(documentResponse.getDocumentId());
        userPostService.save(userEntity);
    }

    @Transactional
    public void uploadCompanyLogo(MultipartFile file) {
        ImageUploadValidator.validateImage(file);
        DocumentResponse documentResponse = documentPostService.uploadCompanyLogo(file);
        CompanyEntity companyEntity = companyGetService.getCompany();
        companyEntity.setLogoImageId(documentResponse.getDocumentId());
        companyPostService.save(companyEntity);
    }

    private void updateUserDetail(ProfileUserUpdateRequest userRequest, UserEntity userEntity) {
        if (StringUtils.isNotBlank(userRequest.getUsername())) {
            String requestUsername = userRequest.getUsername();
            if (!userEntity.getUsername().equals(requestUsername)) {
                Optional<UserEntity> byUsername = userGetService.findByUsername(requestUsername);
                if (byUsername.isPresent()) {
                    throw new UsernameAlreadyUsedException();
                }
                userEntity.setUsername(userRequest.getUsername());
            }
        }
        if (StringUtils.isNotBlank(userRequest.getPassword())) {
            userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        if (StringUtils.isNotBlank(userRequest.getFirstName())) {
            userEntity.setFirstName(userRequest.getFirstName());
        }
        if (StringUtils.isNotBlank(userRequest.getLastName())) {
            userEntity.setLastName(userRequest.getLastName());
        }
        if (StringUtils.isNotBlank(userRequest.getEmail())) {
            userEntity.setEmail(userRequest.getEmail());
        }
        if (StringUtils.isNotBlank(userRequest.getTkn())) {
            userEntity.setTkn(userRequest.getTkn());
        }
        if (StringUtils.isNotBlank(userRequest.getVkn())) {
            userEntity.setVkn(userRequest.getVkn());
        }
    }

    private void updateCompanyDetail(ProfileCompanyUpdateRequest companyRequest, CompanyEntity companyEntity) {
        if (StringUtils.isNotBlank(companyRequest.getCompanyName())) {
            companyEntity.setName(companyRequest.getCompanyName());
        }
        if (StringUtils.isNotBlank(companyRequest.getCompanyAddress())) {
            companyEntity.setAddress(companyRequest.getCompanyAddress());
        }
        if (StringUtils.isNotBlank(companyRequest.getPhoneNumber())) {
            companyEntity.setPhoneNumber(companyRequest.getPhoneNumber());
        }
        if (StringUtils.isNotBlank(companyRequest.getInvoiceUsername())) {
            companyEntity.setInvoiceUsername(companyRequest.getInvoiceUsername());
        }
        if (StringUtils.isNotBlank(companyRequest.getInvoicePassword())) {
            companyEntity.setInvoicePassword(companyRequest.getInvoicePassword());
        }
    }
}
