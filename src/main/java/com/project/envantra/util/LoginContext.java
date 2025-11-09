package com.project.envantra.util;

import com.project.envantra.enums.Role;
import com.project.envantra.model.entity.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginContext {

    private static final ThreadLocal<UserEntity> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(UserEntity userEntity) {
        currentUser.set(userEntity);
    }

    public static UserEntity getUser() {
        return currentUser.get();
    }

    public static Long getUserId() {
        return currentUser.get().getId();
    }

    public static String getUsername() {
        return currentUser.get().getUsername();
    }

    public static String getEmail() {
        return currentUser.get().getEmail();
    }

    public static Role getUserRole() {
        return currentUser.get().getRole();
    }

    public static void clear() {
        currentUser.remove();
    }
}
