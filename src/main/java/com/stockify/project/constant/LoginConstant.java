package com.stockify.project.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginConstant {

    public static final long EXPIRE_DURATION_ONE_DAY = 24L * 60 * 60 * 1000;
    public static final long EXPIRE_DURATION_SEVEN_DAY = 7L * 24 * 60 * 60 * 1000;

    //GURME
    public static final String GURME_ADMIN_USER_NAME_1 = "soner";
    public static final String GURME_ADMIN_USER_PASSWORD_1 = "test1234";
    public static final String GURME_ADMIN_USER_NAME_2 = "selcuk";
    public static final String GURME_ADMIN_USER_PASSWORD_2 = "test4321";
}
