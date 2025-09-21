package com.example.authservice.service;

import com.example.authservice.dto.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 认证服务
 * 封装与Keycloak的交互逻辑
 */
@Service
public class AuthService {
    
    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.resource}")
    private String clientId;
    
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 用户登录
     */
    public LoginResponse login(String username, String password) throws Exception {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", 
                                       keycloakServerUrl, realm);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 公开客户端不需要Basic Auth
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);
        params.add("client_id", clientId);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            @SuppressWarnings("unchecked")
            Map<String, Object> tokenResponse = objectMapper.readValue(response.getBody(), Map.class);
            
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSuccess(true);
            loginResponse.setAccessToken((String) tokenResponse.get("access_token"));
            loginResponse.setRefreshToken((String) tokenResponse.get("refresh_token"));
            loginResponse.setExpiresIn((Integer) tokenResponse.get("expires_in"));
            loginResponse.setTokenType((String) tokenResponse.get("token_type"));
            loginResponse.setMessage("登录成功");
            
            return loginResponse;
        } else {
            throw new RuntimeException("Keycloak认证失败: " + response.getBody());
        }
    }
    
    /**
     * 刷新令牌
     */
    public LoginResponse refreshToken(String refreshToken) throws Exception {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", 
                                       keycloakServerUrl, realm);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 公开客户端不需要Basic Auth
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);
        params.add("client_id", clientId);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            @SuppressWarnings("unchecked")
            Map<String, Object> tokenResponse = objectMapper.readValue(response.getBody(), Map.class);
            
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSuccess(true);
            loginResponse.setAccessToken((String) tokenResponse.get("access_token"));
            loginResponse.setRefreshToken((String) tokenResponse.get("refresh_token"));
            loginResponse.setExpiresIn((Integer) tokenResponse.get("expires_in"));
            loginResponse.setTokenType((String) tokenResponse.get("token_type"));
            loginResponse.setMessage("令牌刷新成功");
            
            return loginResponse;
        } else {
            throw new RuntimeException("令牌刷新失败: " + response.getBody());
        }
    }
    
    /**
     * 用户登出
     */
    public void logout(String refreshToken) throws Exception {
        String logoutUrl = String.format("%s/realms/%s/protocol/openid-connect/logout", 
                                        keycloakServerUrl, realm);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 公开客户端不需要Basic Auth
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("refresh_token", refreshToken);
        params.add("client_id", clientId);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(logoutUrl, request, String.class);
        
        if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new RuntimeException("登出失败: " + response.getBody());
        }
    }
    
    /**
     * 验证令牌并获取用户信息
     */
    public Map<String, Object> verifyToken(String accessToken) throws Exception {
        String userInfoUrl = String.format("%s/realms/%s/protocol/openid-connect/userinfo", 
                                          keycloakServerUrl, realm);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
            return result;
        } else {
            throw new RuntimeException("令牌验证失败: " + response.getBody());
        }
    }
    
    /**
     * 获取用户信息
     */
    public Map<String, Object> getUserInfo(String accessToken) throws Exception {
        return verifyToken(accessToken);
    }
}