package com.stockify.project.converter;

import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.model.request.UserCreationEmailRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailConverter {

    public static UserCreationEmailRequest toEmailRequest(String username, String password,
                                                          UserEntity creatorUser, UserEntity brokerUser) {
        return UserCreationEmailRequest.builder()
                .brokerUsername(username)
                .brokerPassword(password)
                .brokerFirstName(brokerUser.getFirstName())
                .brokerLastName(brokerUser.getLastName())
                .creatorUserFirstName(creatorUser.getFirstName())
                .creatorUserLastName(creatorUser.getLastName())
                .build();
    }
}
