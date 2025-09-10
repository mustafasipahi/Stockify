package com.stockify.project.enums;

import com.stockify.project.exception.StockifyRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TenantType {

    GURME(1);

    private final long tenantId;

    public static TenantType fromValue(Long value) {
        return Arrays.stream(TenantType.values())
                .filter(tenant -> tenant.getTenantId() == value)
                .findFirst()
                .orElseThrow(() -> new StockifyRuntimeException("Tenant type not found"));
    }
}
