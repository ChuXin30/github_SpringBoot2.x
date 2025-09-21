package com.example.userservice.config;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;

/**
 * 微服务用户详情类
 * 封装从API网关传递过来的用户信息
 */
public class MicroserviceUserDetails {
    private final String userId;
    private final String username;
    private final List<GrantedAuthority> authorities;
    private final Map<String, Object> additionalInfo;
    
    public MicroserviceUserDetails(String userId, String username, 
                                 List<GrantedAuthority> authorities, 
                                 Map<String, Object> additionalInfo) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
        this.additionalInfo = additionalInfo;
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public List<GrantedAuthority> getAuthorities() { return authorities; }
    public Map<String, Object> getAdditionalInfo() { return additionalInfo; }
    
    @Override
    public String toString() {
        return "MicroserviceUserDetails{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}