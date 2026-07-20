package com.distributed.ratelimiter.gateway.filter;

import com.distributed.ratelimiter.gateway.client.CheckRequest;
import com.distributed.ratelimiter.gateway.client.CheckResult;
import com.distributed.ratelimiter.gateway.client.RateLimitClient;
import com.distributed.ratelimiter.gateway.client.RateLimitClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class RateLimitFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private final RateLimitClient rateLimitClient;

    public RateLimitFilter(RateLimitClient rateLimitClient) {
        this.rateLimitClient = rateLimitClient;
    }

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        String tenant = request.headers().firstHeader("X-Tenant-Id");

        // sem tenant → fail-open, não rate limita
        if(tenant == null || tenant.isBlank())
            return next.handle(request); // executa o próximo da cadeia e me devolve o resultado

        try{
            CheckResult result = rateLimitClient.check(CheckRequest.of(tenant));

            if(result instanceof CheckResult.Allow allow){
                ServerResponse response = next.handle(request);
                // adiciona headers RateLimit-* na response
                // (ServerResponse é imutável — pesquisa como adicionar headers)
                response.headers().add("RateLimit-Remaining", String.valueOf(allow.remaining()));
                response.headers().add("RateLimit-Limit", String.valueOf(allow.limit()));
                response.headers().add("RateLimit-Reset", String.valueOf(allow.resetAt()));
                return response;
            }else{
                CheckResult.Deny deny = (CheckResult.Deny) result;
                return ServerResponse.status(429)
                        .header("Retry-After", String.valueOf(deny.retryAfter()))
                        .header("RateLimit-Remaining", "0")
                        .header("RateLimit-Limit", String.valueOf(deny.limit()))
                        .header("RateLimit-Reset", String.valueOf(deny.resetAt()))
                        .body(deny);
            }
        } catch (RateLimitClientException e){
            log.warn("Rate limit check failed for tenant={}, failing open", tenant, e);
            return next.handle(request);
        }
    }
}
