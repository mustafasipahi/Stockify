package com.project.envantra.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginConstant {

    public static final long EXPIRE_DURATION_ONE_DAY = 24L * 60 * 60 * 1000;
    public static final long EXPIRE_DURATION_SEVEN_DAY = 7L * 24 * 60 * 60 * 1000;
}
