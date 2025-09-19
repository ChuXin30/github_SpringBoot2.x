package org.example.chapter12.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * API 控制器
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * 公开的 API 端点
     */
    @GetMapping("/public")
    public Map<String, Object> publicApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是一个公开的 API 端点");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 需要认证的 API 端点
     */
    @GetMapping("/protected")
    public Map<String, Object> protectedApi(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是一个受保护的 API 端点");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 需要 ADMIN 角色的 API 端点
     */
    @GetMapping("/admin")
    public Map<String, Object> adminApi(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是一个管理员 API 端点");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
