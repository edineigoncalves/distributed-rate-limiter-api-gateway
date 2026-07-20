package com.distributed.ratelimiter.gateway.client;

import com.distributed.ratelimiter.gateway.dto.CheckResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Component
public class HttpRateLimitClient implements RateLimitClient {

    private static final Logger log = LoggerFactory.getLogger(HttpRateLimitClient.class);

    private final RestClient restClient;

    public HttpRateLimitClient(RestClient rateLimiterRestClient) {
        this.restClient = rateLimiterRestClient;
    }

    @Override
    public CheckResult check(CheckRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        try {
            CheckResponseDTO response = restClient.post()
                    .uri("/v1/check")
                    .body(request)
                    .retrieve()
                    .body(CheckResponseDTO.class);

            return CheckResult.allow(response.remaining(), response.limit(), response.resetAt());

        } catch (Exception e) {
            throw new RateLimitClientException("Failed to check rate limit", e);
        }
    }
}
