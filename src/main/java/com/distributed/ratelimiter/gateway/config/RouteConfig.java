package com.distributed.ratelimiter.gateway.config;

import com.distributed.ratelimiter.gateway.filter.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class RouteConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(RateLimitFilter rateLimitFilter) {
        return route("httpbin-test")
                .GET("/test/**", http())
                .before(uri("https://httpbin.org"))
                .before(stripPrefix(1))
                .filter(rateLimitFilter)
                .build();
    }
}