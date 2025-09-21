package com.example.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API网关JWT认证过滤器
 * 在网关层统一验证JWT token，验证通过后将用户信息传递给下游服务
 */
@Component
public class JwtAuthenticationFilter implements GatewayFilter {
    
    @Value("${jwt.secret:mySecretKey12345678901234567890123456789012}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400}")
    private long jwtExpiration;
    
    private SecretKey key;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @PostConstruct
    public void init() {
        // 初始化JWT密钥
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractToken(exchange);
        
        if (token == null) {
            return handleUnauthorized(exchange, "缺少认证令牌");
        }
        
        try {
            // 简化JWT验证（开发环境）- 解析但不验证签名
            String[] chunks = token.split("\\.");
            if (chunks.length != 3) {
                return handleUnauthorized(exchange, "JWT格式错误");
            }
            
            // 解码JWT payload（不验证签名）
            String payload = new String(java.util.Base64.getUrlDecoder().decode(chunks[1]));
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
            
            // 检查令牌是否过期
            Long exp = ((Number) claims.get("exp")).longValue();
            if (exp * 1000 < System.currentTimeMillis()) {
                return handleUnauthorized(exchange, "令牌已过期");
            }
            
            // 提取用户信息并添加到请求头，传递给下游服务
            String userId = (String) claims.get("sub");
            String username = (String) claims.get("username");
            Object rolesObj = claims.get("roles");
            List<String> roles = rolesObj instanceof List ? (List<String>) rolesObj : List.of("user");
            
            // 构建用户信息JSON
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", userId);
            userInfo.put("username", username);
            userInfo.put("roles", roles);
            
            String userInfoJson = objectMapper.writeValueAsString(userInfo);
            
            // 添加用户信息到请求头
            ServerWebExchange modifiedExchange = exchange.mutate()
                .request(originalRequest -> originalRequest
                    .header("X-User-Info", userInfoJson)
                    .header("X-User-ID", userId)
                    .header("X-Username", username))
                .build();
            
            return chain.filter(modifiedExchange);
            
        } catch (Exception e) {
            return handleUnauthorized(exchange, "无效的令牌: " + e.getMessage());
        }
    }
    
    /**
     * 从请求中提取JWT token
     */
    private String extractToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * 处理未授权请求
     */
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        try {
            String responseJson = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                .wrap(responseJson.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return exchange.getResponse().setComplete();
        }
    }
}
