package com.project.envantra.util;

import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.model.request.UserCreationEmailRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NameUtil {

    public static String getUserFullName(UserEntity userEntity) {
        return getFullName(userEntity.getFirstName(), userEntity.getLastName());
    }

    public static String getUserFullName(UserCreationEmailRequest request) {
        return getFullName(request.getCreatorUserFirstName(), request.getCreatorUserLastName());
    }

    public static String getBrokerFullName(UserCreationEmailRequest request) {
        return getFullName(request.getBrokerFirstName(), request.getBrokerLastName());
    }

    public static String getBrokerFullName(BrokerDto brokerDto) {
        return getFullName(brokerDto.getFirstName(), brokerDto.getLastName());
    }

    public static String getBrokerUsername(BrokerDto brokerDto) {
        return getUsername(brokerDto.getFirstName(), brokerDto.getLastName());
    }

    private static String getFullName(String firstName, String lastName) {
        String fullName = "";
        if (StringUtils.isNotBlank(firstName)) {
            fullName = firstName;
        }
        if (StringUtils.isNotBlank(lastName)) {
            if (StringUtils.isNotBlank(fullName)) {
                fullName = fullName + " " + lastName;
            } else {
                fullName = lastName;
            }
        }
        if (StringUtils.isBlank(fullName)) {
            fullName = "Unknown";
        }
        return fullName;
    }

    private static String getUsername(String firstName, String lastName) {
        String fullName = "";
        if (StringUtils.isNotBlank(firstName)) {
            fullName = firstName;
        }
        if (StringUtils.isNotBlank(lastName)) {
            if (StringUtils.isNotBlank(fullName)) {
                fullName = fullName + "_" + lastName;
            } else {
                fullName = lastName;
            }
        }
        if (StringUtils.isBlank(fullName)) {
            fullName = "unknown_name";
        }
        return fullName;
    }
}
