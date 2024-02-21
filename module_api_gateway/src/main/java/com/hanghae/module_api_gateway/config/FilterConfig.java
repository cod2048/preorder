package com.hanghae.module_api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public RouteLocator gatewayRoute(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(r -> r.path("/api/v1/users/**")
                        .uri("http://localhost:8081"))
                .route(r -> r.path("/api/v1/verification/**")
                        .uri("http://localhost:8081"))
                .route(r -> r.path("/api/v1/items/**")
                        .uri("http://localhost:8082"))
                .route(r -> r.path("/api/v1/orders/**")
                        .uri("http://localhost:8083"))
                .route(r -> r.path("/api/v1/payments/**")
                        .uri("http://localhost:8084"))
                .build();
    }
}
