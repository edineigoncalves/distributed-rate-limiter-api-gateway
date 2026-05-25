package com.distributed.ratelimiter.gateway.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CheckResultTest {

    @Test
    void shouldCreateAllow() {
        CheckResult result = CheckResult.allow(10, 100, 12345L);

        assertThat(result)
                .isInstanceOf(CheckResult.Allow.class);

        assertThat(result.remaining()).isEqualTo(10);
        assertThat(result.limit()).isEqualTo(100);
        assertThat(result.resetAt()).isEqualTo(12345L);
    }

    @Test
    void shouldAllowRemainingZero() {
        CheckResult result = CheckResult.allow(0, 100, 123L);

        assertThat(result.remaining()).isZero();
    }

    @Test
    void shouldCreateDenyWithRemainingZero() {
        CheckResult result = CheckResult.deny(100, 12345L, 10);

        assertThat(result)
                .isInstanceOf(CheckResult.Deny.class);

        assertThat(result.remaining()).isZero();
        assertThat(result.limit()).isEqualTo(100);
    }

    @Test
    void shouldThrowWhenAllowRemainingNegative() {
        assertThatThrownBy(() -> CheckResult.allow(-1, 100, 123L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("remaining");
    }

    @Test
    void shouldThrowWhenAllowLimitInvalid() {
        assertThatThrownBy(() -> CheckResult.allow(1, 0, 123L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("limit");
    }

    @Test
    void shouldThrowWhenDenyRetryAfterInvalid() {
        assertThatThrownBy(() -> CheckResult.deny(100, 123L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("retryAfter");
    }

    @Test
    void shouldThrowWhenDenyLimitInvalid() {
        assertThatThrownBy(() -> CheckResult.deny(0, 123L, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("limit");
    }
}