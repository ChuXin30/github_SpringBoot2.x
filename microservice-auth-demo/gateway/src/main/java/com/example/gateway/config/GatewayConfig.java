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
                .uri("http://localhost:8082"))  // 直连用户服务
            
            // 认证服务路由（直接转发，不验证JWT）
            .route("auth-service", r -> r.path("/auth/**")
                .uri("http://localhost:8081"))  // 直连认证服务
            
            // 根路径重定向到index.html
            .route("root-redirect", r -> r.path("/")
                .filters(f -> f.redirect(302, "/index.html"))
                .uri("no://op"))
                
            .build();
    }
}
