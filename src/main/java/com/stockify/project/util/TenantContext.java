package com.stockify.project.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantContext {

    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(Long tenant) {
        currentTenant.set(tenant);
    }

    public static Long getTenantId() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
