package com.stockify.project.converter;

import com.stockify.project.enums.Role;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.model.dto.UserDto;
import com.stockify.project.model.request.BrokerCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUserRole;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final PasswordEncoder passwordEncoder;

    public UserDto toDto(BrokerCreateRequest request, String username, String password) {
        return UserDto.builder()
                .username(username)
                .password(password)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .tenantId(getTenantId())
                .role(getRole())
                .build();
    }

    public UserEntity toEntity(UserDto userDto) {
        return UserEntity.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .tenantId(userDto.getTenantId())
                .role(userDto.getRole())
                .build();
    }

    private Role getRole() {
        Role role = getUserRole();
        boolean isCreatorAdmin = role == Role.ROLE_ADMIN;
        if (isCreatorAdmin) {
            return Role.ROLE_BROKER;
        }
        boolean creatorBroker = role == Role.ROLE_BROKER;
        if (creatorBroker) {
            return Role.ROLE_USER;
        }
        return Role.ROLE_USER;
    }
}
