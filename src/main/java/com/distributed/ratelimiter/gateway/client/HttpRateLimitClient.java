package com.distributed.ratelimiter.gateway.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HttpRateLimitClient implements RateLimitClient {

    private static final Logger log = LoggerFactory.getLogger(HttpRateLimitClient.class);

    @Override
    public CheckResult check(CheckRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        log.info("HttpRateLimitClient stub called for tenant={}", request.tenant());
        return CheckResult.allow(99, 100, System.currentTimeMillis() / 1000 + 60);
    }
}
