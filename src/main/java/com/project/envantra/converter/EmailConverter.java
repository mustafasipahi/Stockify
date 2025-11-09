package com.project.envantra.converter;

import com.project.envantra.model.dto.UserSecurityDto;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.model.request.UserCreationEmailRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.project.envantra.util.LoginContext.getUser;

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
