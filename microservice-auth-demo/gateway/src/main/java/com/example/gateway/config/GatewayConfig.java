package com.example.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // 直连路由 - 立即可用（优先级高）
            .route("auth-service-direct", r -> r.path("/auth/**")
                .uri("http://localhost:8081"))  // 直连认证服务
            
            .route("user-service-direct", r -> r.path("/api/user/**")
                .filters(f -> f
                    .stripPrefix(1)  // 移除 /api 前缀
                    .filter(jwtAuthenticationFilter))  // JWT验证过滤器
                .uri("http://localhost:8082"))  // 直连用户服务
            
            // Nacos负载均衡路由 - 当网关注册后生效（优先级低）
            .route("auth-service-lb", r -> r.path("/auth-lb/**")
                .filters(f -> f.rewritePath("/auth-lb/(?<segment>.*)", "/auth/${segment}"))
                .uri("lb://auth-service"))  // Nacos负载均衡
            
            .route("user-service-lb", r -> r.path("/api/user-lb/**")
                .filters(f -> f
                    .rewritePath("/api/user-lb/(?<segment>.*)", "/user/${segment}")
                    .filter(jwtAuthenticationFilter))
                .uri("lb://user-service"))  // Nacos负载均衡
            
            // 支持传统路由作为备用（开发环境）
            .route("user-service-fallback", r -> r.path("/api/user-direct/**")
                .filters(f -> f
                    .rewritePath("/api/user-direct/(?<segment>.*)", "/user/${segment}")
                    .filter(jwtAuthenticationFilter))
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
