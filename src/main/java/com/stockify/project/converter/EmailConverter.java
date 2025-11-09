package com.stockify.project.converter;

import com.stockify.project.model.dto.UserSecurityDto;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.model.request.UserCreationEmailRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.stockify.project.util.LoginContext.getUser;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailConverter {

    public static UserCreationEmailRequest toEmailRequest(UserSecurityDto userSecurity, UserEntity brokerUser) {
        UserEntity creatorUser = getUser();
        return UserCreationEmailRequest.builder()
                .brokerUsername(userSecurity.getUsername())
                .brokerPassword(userSecurity.getPassword())
                .brokerFirstName(brokerUser.getFirstName())
                .brokerLastName(brokerUser.getLastName())
                .creatorUserFirstName(creatorUser.getFirstName())
                .creatorUserLastName(creatorUser.getLastName())
                .build();
    }
}
