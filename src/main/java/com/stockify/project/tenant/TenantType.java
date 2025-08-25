package com.stockify.project.tenant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TenantType {

    PUBLIC("public"),
    GURME("stokify_gurme");

    private final String stokifySchemaName;
}
