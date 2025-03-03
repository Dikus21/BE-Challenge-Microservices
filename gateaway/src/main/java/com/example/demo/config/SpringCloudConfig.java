package com.example.demo.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudConfig {

    //    referensi https://spring.io/projects/spring-cloud-gateway
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/v1/user/**")
                        .uri("http://localhost:9091/")
                        .id("userServices"))

                .route(r -> r.path("/api/v1/order/**", "/api/v1/product/**", "/api/v1/orderDetail/**")
                        .uri("http://localhost:9092/")
                        .id("orderAndProductServices"))

                .route(r -> r.path("/api/v1/merchant/**")
                        .uri("http://localhost:9093/")
                        .id("merchantServices"))
                .build();
    }

}
