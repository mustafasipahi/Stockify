package com.project.envantra.model.dto;

import com.project.envantra.model.response.InvoiceTokenResponse;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;

@Getter
public class InvoiceTokenCacheDto implements Serializable {

    private static final long CACHE_DURATION_SECONDS = 300;
    private final InvoiceTokenResponse token;
    private final Instant expiryTime;

    public InvoiceTokenCacheDto(InvoiceTokenResponse token) {
        this.token = token;
        this.expiryTime = Instant.now().plusSeconds(CACHE_DURATION_SECONDS);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryTime);
    }
}
