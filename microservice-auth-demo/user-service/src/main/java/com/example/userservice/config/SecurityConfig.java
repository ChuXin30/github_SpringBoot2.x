package com.example.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 微服务安全配置
 * 
 * 在微服务架构中，这里的配置相对简化：
 * 1. JWT认证已在API网关完成
 * 2. 这里主要处理用户信息提取和权限验证
 * 3. 支持方法级别的权限控制
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF，因为是无状态的REST API
            .csrf(csrf -> csrf.disable())
            
            // 设置会话管理为无状态
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置请求授权
            .authorizeHttpRequests(authz -> authz
                // 健康检查端点允许匿名访问
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                
                // 公开API端点
                .requestMatchers("/api/public/**").permitAll()
                
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            
            // 添加自定义的用户信息提取过滤器
            .addFilterBefore(new UserInfoExtractionFilter(), 
                UsernamePasswordAuthenticationFilter.class)
            
            // 禁用默认的表单登录和HTTP Basic认证
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
