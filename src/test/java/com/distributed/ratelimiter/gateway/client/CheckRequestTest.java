package com.distributed.ratelimiter.gateway.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CheckRequestTest {

    @Test
    void shouldCreateRequestWithAllFields() {
        CheckRequest request = new CheckRequest("tenant1", "user1", "/api", "GET", 2);

        assertThat(request.tenant()).isEqualTo("tenant1");
        assertThat(request.user()).isEqualTo("user1");
        assertThat(request.route()).isEqualTo("/api");
        assertThat(request.method()).isEqualTo("GET");
        assertThat(request.cost()).isEqualTo(2);
    }

    @Test
    void shouldCreateUsingFactoryMethod() {
        CheckRequest request = CheckRequest.of("tenant1");

        assertThat(request.tenant()).isEqualTo("tenant1");
        assertThat(request.user()).isNull();
        assertThat(request.route()).isNull();
        assertThat(request.method()).isNull();
        assertThat(request.cost()).isEqualTo(1);
    }

    @Test
    void shouldThrowWhenTenantIsNull() {
        assertThatThrownBy(() -> new CheckRequest(null, null, null, null, 1))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("tenant");
    }

    @Test
    void shouldThrowWhenTenantIsEmpty() {
        assertThatThrownBy(() -> new CheckRequest("", null, null, null, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tenant");
    }

    @Test
    void shouldThrowWhenTenantIsBlank() {
        assertThatThrownBy(() -> new CheckRequest("   ", null, null, null, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tenant");
    }
}