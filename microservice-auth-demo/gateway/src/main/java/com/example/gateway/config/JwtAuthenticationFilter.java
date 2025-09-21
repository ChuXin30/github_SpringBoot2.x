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
            // 验证JWT
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            // 检查令牌是否过期
            if (claims.getExpiration().before(new Date())) {
                return handleUnauthorized(exchange, "令牌已过期");
            }
            
            // 提取用户信息并添加到请求头，传递给下游服务
            String userId = claims.getSubject();
            String username = claims.get("username", String.class);
            List<String> roles = claims.get("roles", List.class);
            
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
