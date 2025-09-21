package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * 静态资源配置
 * 让API网关提供前端静态文件服务
 */
@Configuration
public class StaticResourceConfig {

    /**
     * 配置静态资源路由
     */
    @Bean
    public RouterFunction<ServerResponse> staticResourceRouter() {
        return RouterFunctions.route()
            // 提供所有静态资源（优先级最低）
            .resources("/static/**", new ClassPathResource("static/"))
            .resources("/**", new ClassPathResource("static/"))
            
            .build();
    }
}
