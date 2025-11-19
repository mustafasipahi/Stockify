package com.project.envantra.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.project.envantra.constant.LoginConstant.EXPIRE_DURATION_ONE_DAY;
import static com.project.envantra.constant.LoginConstant.EXPIRE_DURATION_SEVEN_DAY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtil {

    public static Date getTokenExpirationDate(boolean rememberMe) {
        long duration = getTokenExpirationTime(rememberMe);
        return new Date(System.currentTimeMillis() + duration);
    }

    public static long getTokenExpirationTime(boolean rememberMe) {
        return rememberMe ? EXPIRE_DURATION_SEVEN_DAY : EXPIRE_DURATION_ONE_DAY;
    }
}
