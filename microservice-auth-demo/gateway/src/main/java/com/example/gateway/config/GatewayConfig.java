package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API网关路由配置
 * 负责请求路由和JWT预验证
 */
@Configuration
public class GatewayConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // 用户服务路由
            .route("user-service", r -> r.path("/api/user/**")
                .filters(f -> f
                    .stripPrefix(1)  // 移除 /api 前缀
                    .filter(new JwtAuthenticationFilter()))  // JWT验证过滤器
                .uri("lb://user-service"))  // 负载均衡到用户服务
            
            // 订单服务路由
            .route("order-service", r -> r.path("/api/order/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .filter(new JwtAuthenticationFilter()))
                .uri("lb://order-service"))
            
            // 认证服务路由（直接转发，不验证JWT）
            .route("auth-service", r -> r.path("/auth/**")
                .uri("http://keycloak:8080"))
            
            // 公开API（无需认证）
            .route("public-api", r -> r.path("/public/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://public-service"))
                
            .build();
    }
}
