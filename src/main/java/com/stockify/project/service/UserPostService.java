package com.stockify.project.service;

import com.stockify.project.converter.UserConverter;
import com.stockify.project.model.dto.UserDto;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.model.request.BrokerCreateRequest;
import com.stockify.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPostService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Transactional
    public void save(UserEntity user) {
        userRepository.save(user);
    }

    @Transactional
    public void create(UserDto userDto) {
        userRepository.save(userConverter.toEntity(userDto));
    }

    @Transactional
    public UserEntity saveBrokerUser(BrokerCreateRequest request, String username, String password) {
        UserDto userDto = userConverter.toDto(request, username, password);
        UserEntity user = userConverter.toEntity(userDto);
        return userRepository.save(user);
    }
}
