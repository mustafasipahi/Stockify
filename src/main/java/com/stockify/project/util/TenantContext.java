package com.stockify.project.util;

import com.stockify.project.model.entity.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantContext {

    private static final ThreadLocal<Long> currentTenantId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();
    private static final ThreadLocal<String> currentEmail = new ThreadLocal<>();

    public static void setCurrentTenantId(UserEntity userEntity) {
        currentTenantId.set(userEntity.getTenantId());
        currentUsername.set(userEntity.getUsername());
        currentEmail.set(userEntity.getEmail());
    }

    public static Long getTenantId() {
        return currentTenantId.get();
    }

    public static String getUsername() {
        return currentUsername.get();
    }

    public static String getEmail() {
        return currentEmail.get();
    }

    public static void clear() {
        currentTenantId.remove();
        currentUsername.remove();
        currentEmail.remove();
    }
}
