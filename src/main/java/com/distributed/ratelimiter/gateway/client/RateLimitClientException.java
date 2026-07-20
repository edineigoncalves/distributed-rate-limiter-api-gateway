package com.distributed.ratelimiter.gateway.client;

public class RateLimitClientException extends RuntimeException {

    public RateLimitClientException(String message) {
        super(message);
    }

    public RateLimitClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
