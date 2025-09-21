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
            // 用户服务路由 - 临时使用直连（优先级高）
            .route("user-service-direct", r -> r.path("/api/user/**")
                .filters(f -> f
                    .stripPrefix(1)  // 移除 /api 前缀
                    .filter(new JwtAuthenticationFilter()))  // JWT验证过滤器
                .uri("http://localhost:8082"))  // 直连用户服务
            
            // 认证服务路由 - 临时使用直连（优先级高）
            .route("auth-service-direct", r -> r.path("/auth/**")
                .uri("http://localhost:8081"))  // 直连认证服务
            
            // Nacos路由（作为备用，优先级低）
            .route("user-service-nacos", r -> r.path("/api/user-nacos/**")
                .filters(f -> f
                    .rewritePath("/api/user-nacos/(?<segment>.*)", "/user/${segment}")
                    .filter(new JwtAuthenticationFilter()))
                .uri("lb://user-service"))  // Nacos负载均衡
            
            .route("auth-service-nacos", r -> r.path("/auth-nacos/**")
                .filters(f -> f.rewritePath("/auth-nacos/(?<segment>.*)", "/auth/${segment}"))
                .uri("lb://auth-service"))  // Nacos负载均衡
            
            // 支持传统路由作为备用（开发环境）
            .route("user-service-fallback", r -> r.path("/api/user-direct/**")
                .filters(f -> f
                    .rewritePath("/api/user-direct/(?<segment>.*)", "/user/${segment}")
                    .filter(new JwtAuthenticationFilter()))
                .uri("http://localhost:8082"))  // 直连备用
                
            .route("auth-service-fallback", r -> r.path("/auth-direct/**")
                .filters(f -> f.rewritePath("/auth-direct/(?<segment>.*)", "/auth/${segment}"))
                .uri("http://localhost:8081"))  // 直连备用
            
            // 根路径重定向到index.html
            .route("root-redirect", r -> r.path("/")
                .filters(f -> f.redirect(302, "/index.html"))
                .uri("no://op"))
                
            .build();
    }
}
