package com.stockify.project.util;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.request.UserCreationEmailRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NameUtil {

    public static String getUserFullName(UserCreationEmailRequest request) {
        return getFullName(request.getCreatorUserFirstName(), request.getCreatorUserLastName());
    }

    public static String getBrokerFullName(UserCreationEmailRequest request) {
        return getFullName(request.getBrokerFirstName(), request.getBrokerLastName());
    }

    public static String getBrokerFullName(BrokerDto brokerDto) {
        return getFullName(brokerDto.getFirstName(), brokerDto.getLastName());
    }

    private static String getFullName(String firstName, String lastName) {
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
