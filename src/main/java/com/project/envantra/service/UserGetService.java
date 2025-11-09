package com.project.envantra.service;

import com.project.envantra.converter.UserConverter;
import com.project.envantra.exception.UserNotFoundException;
import com.project.envantra.model.dto.UserDto;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.project.envantra.util.LoginContext.getUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGetService {

    private final UserRepository userRepository;

    public UserEntity findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public UserDto getLoginUserDetail() {
        return UserConverter.toDto(getUser());
    }

    public List<UserEntity> findAllByIdIn(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
