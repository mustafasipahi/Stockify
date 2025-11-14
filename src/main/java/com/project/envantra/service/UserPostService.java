package com.project.envantra.service;

import com.project.envantra.converter.UserConverter;
import com.project.envantra.generator.UserInfoGenerator;
import com.project.envantra.model.dto.UserDto;
import com.project.envantra.model.dto.UserSecurityDto;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.model.request.BrokerCreateRequest;
import com.project.envantra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPostService {

    private final UserRepository userRepository;
    private final CompanyPostService companyPostService;
    private final UserInfoGenerator userInfoGenerator;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void save(UserEntity user) {
        userRepository.save(user);
    }

    @Transactional
    public UserEntity save(UserDto userDto) {
        String encryptedPassword = passwordEncoder.encode(userDto.getPassword());
        return userRepository.save(UserConverter.toEntity(userDto, userDto.getUsername(), encryptedPassword));
    }

    @Transactional
    public UserEntity createNewUser(BrokerCreateRequest request) {
        UserSecurityDto userSecurity = userInfoGenerator.generate(request.getFirstName(), request.getLastName());
        String encryptedPassword = passwordEncoder.encode(userSecurity.getPassword());
        UserEntity userEntity = UserConverter.toEntity(request, userSecurity.getUsername(), encryptedPassword);
        UserEntity savedNewUser = userRepository.save(userEntity);
        companyPostService.saveBrokerCompany(savedNewUser.getId());
        return savedNewUser;
    }
}
