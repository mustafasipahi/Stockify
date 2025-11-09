package com.stockify.project.service;

import com.stockify.project.converter.UserConverter;
import com.stockify.project.generator.UserInfoGenerator;
import com.stockify.project.model.dto.UserDto;
import com.stockify.project.model.dto.UserSecurityDto;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.model.request.BrokerCreateRequest;
import com.stockify.project.repository.UserRepository;
import com.stockify.project.service.email.UserCreationEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.converter.EmailConverter.toEmailRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPostService {

    private final UserRepository userRepository;
    private final CompanyPostService companyPostService;
    private final UserInfoGenerator userInfoGenerator;
    private final UserCreationEmailService userCreationEmailService;
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
        userCreationEmailService.sendUserCreationNotification(toEmailRequest(userSecurity, savedNewUser));
        return savedNewUser;
    }
}
