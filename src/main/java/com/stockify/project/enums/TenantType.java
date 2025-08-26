package com.stockify.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TenantType {

    GURME(1);

    private final long tenantId;
}
