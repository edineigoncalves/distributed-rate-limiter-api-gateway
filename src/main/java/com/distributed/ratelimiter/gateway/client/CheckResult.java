package com.distributed.ratelimiter.gateway.client;

public sealed interface CheckResult
        permits CheckResult.Allow, CheckResult.Deny {

    int remaining();     // métodos comuns que TODOS subtipos têm
    int limit();
    long resetAt();

    record Allow(int remaining, int limit, long resetAt)
            implements CheckResult {}

    record Deny(int remaining, int limit, long resetAt, long retryAfter)
            implements CheckResult {}
}