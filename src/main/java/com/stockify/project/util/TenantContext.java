package com.stockify.project.util;

import com.stockify.project.model.entity.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantContext {

    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();

    public static void setCurrentTenant(UserEntity userEntity) {
        currentTenant.set(userEntity.getTenantId());
        currentUsername.set(userEntity.getUsername());
    }

    public static Long getTenantId() {
        return currentTenant.get();
    }

    public static String getUsername() {
        return currentUsername.get();
    }

    public static void clear() {
        currentTenant.remove();
        currentUsername.remove();
    }
}
