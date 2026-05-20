package com.distributed.ratelimiter.gateway.client;

import java.util.Objects;

public record CheckRequest(
        String tenant,
        String user,
        String route,
        String method,
        int cost
) {
    // Compact constructor — roda ANTES da atribuição dos campos.
    // Note: sem parênteses de parâmetros — eles vêm do header do record.
    public CheckRequest {
        Objects.requireNonNull(tenant, "tenant must not be null");
        if (tenant.isBlank()) {
            throw new IllegalArgumentException("tenant must not be blank");
        }
    }

    // Factory method estático: cria CheckRequest com defaults.
    public static CheckRequest of(String tenant) {
        return new CheckRequest(tenant, null, null, null, 1);
    }
}