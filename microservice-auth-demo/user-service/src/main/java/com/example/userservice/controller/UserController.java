package com.example.userservice.controller;

import com.example.userservice.config.MicroserviceUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务控制器
 * 演示微服务中如何使用Spring Security进行权限控制
 */
@RestController
@RequestMapping("/user")
public class UserController {
    
    /**
     * 获取当前用户信息
     * 所有认证用户都可以访问
     */
    @GetMapping("/profile")
    public Map<String, Object> getUserProfile(Authentication authentication) {
        MicroserviceUserDetails userDetails = (MicroserviceUserDetails) authentication.getPrincipal();
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userDetails.getUserId());
        response.put("username", userDetails.getUsername());
        response.put("authorities", userDetails.getAuthorities());
        response.put("additionalInfo", userDetails.getAdditionalInfo());
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * 获取用户列表
     * 只有管理员可以访问
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getUserList(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户列表数据 - 仅管理员可见");
        response.put("requestedBy", authentication.getName());
        response.put("users", java.util.Arrays.asList(
            Map.of("id", 1, "username", "user1", "role", "USER"),
            Map.of("id", 2, "username", "user2", "role", "USER"),
            Map.of("id", 3, "username", "admin", "role", "ADMIN")
        ));
        
        return response;
    }
    
    /**
     * 更新用户信息
     * 用户可以更新自己的信息，管理员可以更新任何用户的信息
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public Map<String, Object> updateUser(@PathVariable String userId, 
                                        @RequestBody Map<String, Object> updateData,
                                        Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户信息更新成功");
        response.put("updatedUserId", userId);
        response.put("updatedBy", authentication.getName());
        response.put("updateData", updateData);
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * 删除用户
     * 只有管理员可以删除用户
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> deleteUser(@PathVariable String userId,
                                        Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户删除成功");
        response.put("deletedUserId", userId);
        response.put("deletedBy", authentication.getName());
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * 公开API端点
     * 不需要认证即可访问
     */
    @GetMapping("/public/info")
    public Map<String, Object> getPublicInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是公开的用户服务信息");
        response.put("version", "1.0.0");
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
}
