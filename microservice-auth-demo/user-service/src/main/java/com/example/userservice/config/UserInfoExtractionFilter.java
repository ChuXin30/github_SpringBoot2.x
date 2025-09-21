package com.example.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息提取过滤器
 * 
 * 从API网关传递的请求头中提取用户信息，并设置到Spring Security上下文中
 * 这样微服务就可以获取到当前用户的认证信息
 */
public class UserInfoExtractionFilter extends OncePerRequestFilter {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 从网关传递的请求头中获取用户信息
            String userInfoHeader = request.getHeader("X-User-Info");
            String userId = request.getHeader("X-User-ID");
            String username = request.getHeader("X-Username");
            
            if (userInfoHeader != null && userId != null) {
                // 解析用户信息JSON
                @SuppressWarnings("unchecked")
                Map<String, Object> userInfo = objectMapper.readValue(userInfoHeader, Map.class);
                
                // 提取角色信息
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) userInfo.getOrDefault("roles", new ArrayList<>());
                
                // 转换为Spring Security的权限格式
                List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
                
                // 创建认证对象
                MicroserviceUserDetails userDetails = new MicroserviceUserDetails(
                    userId, username, authorities, userInfo);
                
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities);
                
                // 设置到安全上下文中
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.debug("已设置用户认证信息: " + username + ", 角色: " + roles);
            }
            
        } catch (Exception e) {
            logger.error("解析用户信息失败: " + e.getMessage(), e);
            // 不阻止请求继续，让后续的安全检查处理未认证的情况
        }
        
        filterChain.doFilter(request, response);
    }
}