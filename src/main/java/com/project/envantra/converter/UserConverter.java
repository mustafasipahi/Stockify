package com.project.envantra.converter;

import com.project.envantra.enums.Role;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.model.dto.UserDto;
import com.project.envantra.model.request.BrokerCreateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.project.envantra.util.LoginContext.getUserRole;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConverter {

    public static UserDto toDto(UserEntity userEntity) {
        return UserDto.builder()
                .profileImageId(userEntity.getProfileImageId())
                .username(userEntity.getUsername())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .tkn(userEntity.getTkn())
                .vkn(userEntity.getVkn())
                .build();
    }

    public static UserEntity toEntity(BrokerCreateRequest request, String username, String encryptedPassword) {
        return UserEntity.builder()
                .username(username)
                .password(encryptedPassword)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .tkn(request.getTkn())
                .vkn(request.getVkn())
                .role(getRole())
                .build();
    }

    public static UserEntity toEntity(UserDto userDto, String username, String encryptedPassword) {
        return UserEntity.builder()
                .username(username)
                .password(encryptedPassword)
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .build();
    }

    private static Role getRole() {
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
