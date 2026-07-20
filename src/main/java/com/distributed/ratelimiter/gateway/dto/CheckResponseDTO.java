package com.distributed.ratelimiter.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CheckResponseDTO(
        int remaining,
        int limit,
        @JsonProperty("reset_at") long resetAt
) {}