package com.distributed.ratelimiter.gateway.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class HttpRateLimitClient implements RateLimitClient{

    private static final Logger log = LoggerFactory.getLogger(HttpRateLimitClient.class);

    @Override
    public CheckResult check(CheckRequest request) {
        log.info("HttpRateLimitClient stub called for tenant={}", request.tenant());
        return CheckResult.allow(99,100, System.currentTimeMillis()/100+60);
    }
}
