package com.distributed.ratelimiter.gateway.client;

public interface RateLimitClient {

    CheckResult check(CheckRequest request);
}
