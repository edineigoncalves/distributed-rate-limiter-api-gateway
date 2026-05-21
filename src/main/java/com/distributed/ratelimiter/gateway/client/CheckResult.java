package com.distributed.ratelimiter.gateway.client;

public sealed interface CheckResult
        permits CheckResult.Allow, CheckResult.Deny {

    int remaining(); // real capacity available in bucket after decision
    int limit(); //total capacity bucket
    long resetAt(); //Unix timestamp when the bucket will refill"

    static CheckResult allow(int remaining, int limit, long resetAt) {
        return new Allow(remaining, limit, resetAt);
    }

    static CheckResult deny(int limit, long resetAt, long retryAfter) {
        return new Deny(0, limit, resetAt, retryAfter);
    }

    record Allow(int remaining, int limit, long resetAt) implements CheckResult {
        public Allow {
            if (remaining < 0) {
                throw new IllegalArgumentException("remaining must not be negative");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("limit must be positive");
            }
        }
    }

    record Deny(int remaining, int limit, long resetAt, long retryAfter) implements CheckResult {
        public Deny {
            if (remaining < 0) {
                throw new IllegalArgumentException("remaining must not be negative");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("limit must be positive");
            }
            if (retryAfter <= 0) {
                throw new IllegalArgumentException("retryAfter must be positive");
            }
        }
    }
}