package com.distributed.ratelimiter.gateway.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class HttpRateLimitClientTest {

    private final HttpRateLimitClient client = new HttpRateLimitClient();

    @Test
    void shouldReturnAllow() {
        CheckResult result = client.check(CheckRequest.of("tenant1"));

        assertThat(result)
                .isInstanceOf(CheckResult.Allow.class);
    }

    @Test
    void shouldReturnExpectedValues() {
        CheckResult result = client.check(CheckRequest.of("tenant1"));

        assertThat(result.remaining()).isEqualTo(99);
        assertThat(result.limit()).isEqualTo(100);
    }

    @Test
    void shouldReturnResetAtInFuture() {
        long now = System.currentTimeMillis() / 1000;

        CheckResult result = client.check(CheckRequest.of("tenant1"));

        assertThat(result.resetAt())
                .isGreaterThan(now);
    }

    @Test
    void shouldThrowWhenRequestIsNull() {
        assertThatThrownBy(() -> client.check(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("request");
    }
}