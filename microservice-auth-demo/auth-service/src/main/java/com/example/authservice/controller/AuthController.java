package com.example.authservice.controller;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import com.example.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证服务控制器
 * 负责与Keycloak交互，处理用户登录、登出等认证操作
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 用户登录
     * 向Keycloak请求访问令牌
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest.getUsername(), 
                                                     loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("登录失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody Map<String, String> refreshRequest) {
        try {
            String refreshToken = refreshRequest.get("refreshToken");
            LoginResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("令牌刷新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody Map<String, String> logoutRequest) {
        try {
            String refreshToken = logoutRequest.get("refreshToken");
            authService.logout(refreshToken);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "登出成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "登出失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 验证令牌
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody Map<String, String> tokenRequest) {
        try {
            String accessToken = tokenRequest.get("accessToken");
            Map<String, Object> userInfo = authService.verifyToken(accessToken);
            
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "userInfo", userInfo
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", "令牌无效: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String authorization) {
        try {
            String accessToken = authorization.substring(7); // 移除 "Bearer " 前缀
            Map<String, Object> userInfo = authService.getUserInfo(accessToken);
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "获取用户信息失败",
                "message", e.getMessage()
            ));
        }
    }
}
