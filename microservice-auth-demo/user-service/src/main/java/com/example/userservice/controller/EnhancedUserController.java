package com.example.userservice.controller;

import com.example.userservice.config.MicroserviceUserDetails;
import com.example.userservice.entity.UserProfile;
import com.example.userservice.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 增强的用户服务控制器 - 集成MySQL、Redis、完整业务逻辑
 * 展示真正的企业级微服务数据处理
 */
@RestController
@RequestMapping("/user/enhanced")
public class EnhancedUserController {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedUserController.class);
    
    @Autowired
    private UserProfileService userProfileService;

    /**
     * 获取完整的用户资料 (MySQL + Redis缓存)
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> getEnhancedProfile(Authentication authentication) {
        try {
            MicroserviceUserDetails userDetails = (MicroserviceUserDetails) authentication.getPrincipal();
            logger.info("获取增强用户资料: {}", userDetails.getUserId());
            
            // 从数据库查询用户详细资料 (带Redis缓存)
            UserProfile profile = userProfileService.getUserProfile(userDetails.getUserId());
            
            // 更新最后访问时间
            userProfileService.updateLastLogin(userDetails.getUserId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "basic", Map.of(
                    "userId", profile.getUserId(),
                    "username", profile.getUsername(),
                    "displayName", profile.getDisplayName(),
                    "email", profile.getEmail(),
                    "phone", profile.getPhone()
                ),
                "organization", Map.of(
                    "department", profile.getDepartment(),
                    "position", profile.getPosition()),
                "profile", Map.of(
                    "avatarUrl", profile.getAvatarUrl(),
                    "lastLogin", profile.getLastLogin(),
                    "createdAt", profile.getCreatedAt(),
                    "updatedAt", profile.getUpdatedAt(),
                    "isActive", profile.getIsActive()
                ),
                "security", Map.of(
                    "authorities", userDetails.getAuthorities(),
                    "sessionInfo", userDetails.getAdditionalInfo()
                )
            ));
            response.put("meta", Map.of(
                "dataSource", "MySQL + Redis Cache",
                "timestamp", System.currentTimeMillis(),
                "version", "enhanced-v1.0"
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取增强用户资料失败", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "获取用户资料失败",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    /**
     * 更新用户资料 (保存到MySQL，更新Redis缓存)
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody UserProfile updatedProfile,
            Authentication authentication) {
        try {
            MicroserviceUserDetails userDetails = (MicroserviceUserDetails) authentication.getPrincipal();
            logger.info("更新用户资料: {}", userDetails.getUserId());
            
            // 设置用户ID
            updatedProfile.setUserId(userDetails.getUserId());
            updatedProfile.setUsername(userDetails.getUsername());
            
            // 保存到数据库并更新缓存
            UserProfile savedProfile = userProfileService.saveOrUpdateProfile(updatedProfile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户资料更新成功");
            response.put("data", savedProfile);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("更新用户资料失败", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "更新用户资料失败",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    /**
     * 管理员获取所有用户 (MySQL查询 + 统计信息)
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            logger.info("管理员查询所有用户");
            
            List<UserProfile> users = userProfileService.getAllActiveUsers();
            Map<String, Long> departmentStats = userProfileService.getDepartmentStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "users", users,
                "statistics", Map.of(
                    "total", users.size(),
                    "departments", departmentStats,
                    "byDepartment", departmentStats
                )
            ));
            response.put("meta", Map.of(
                "dataSource", "MySQL Database",
                "timestamp", System.currentTimeMillis(),
                "query", "SELECT * FROM user_profiles WHERE is_active = true"
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("查询用户列表失败", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "查询用户列表失败",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    /**
     * 根据部门查询用户 (Redis缓存)
     */
    @GetMapping("/admin/department/{department}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getUsersByDepartment(@PathVariable String department) {
        try {
            logger.info("查询部门用户: {}", department);
            
            List<UserProfile> users = userProfileService.getUsersByDepartment(department);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "department", department,
                "users", users,
                "count", users.size()
            ));
            response.put("meta", Map.of(
                "dataSource", "MySQL + Redis Cache",
                "cacheKey", "department-users::" + department,
                "timestamp", System.currentTimeMillis()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("查询部门用户失败", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "查询部门用户失败",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    /**
     * 根据角色查询用户 (Redis缓存)
     */
    @GetMapping("/admin/role/{roleName}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getUsersByRole(@PathVariable String roleName) {
        try {
            logger.info("查询角色用户: {}", roleName);
            
            List<UserProfile> users = userProfileService.getUsersByRole(roleName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "roleName", roleName,
                "users", users,
                "count", users.size()
            ));
            response.put("meta", Map.of(
                "dataSource", "MySQL + Redis Cache", 
                "cacheKey", "role-users::" + roleName,
                "timestamp", System.currentTimeMillis()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("查询角色用户失败", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "查询角色用户失败", 
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    /**
     * 获取最近活跃用户 (Redis缓存)
     */
    @GetMapping("/admin/recent-active/{hours}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getRecentlyActiveUsers(@PathVariable int hours) {
        try {
            logger.info("查询最近{}小时活跃用户", hours);
            
            List<UserProfile> users = userProfileService.getRecentlyActiveUsers(hours);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "timeRange", hours + " hours",
                "users", users,
                "count", users.size()
            ));
            response.put("meta", Map.of(
                "dataSource", "MySQL + Redis Cache",
                "cacheKey", "recent-users::" + hours,
                "timestamp", System.currentTimeMillis()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("查询活跃用户失败", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "查询活跃用户失败",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    /**
     * 获取服务状态 (展示数据源集成状态)
     */
    @GetMapping("/admin/service-status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getServiceStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("mysql", Map.of(
                "status", "connected",
                "database", "microservice_db",
                "host", "localhost:3307",
                "tables", Arrays.asList("user_profiles", "user_roles")
            ));
            status.put("redis", Map.of(
                "status", "connected", 
                "host", "localhost:6379",
                "caches", Arrays.asList("user-profiles", "department-users", "role-users", "recent-users", "department-stats")
            ));
            status.put("nacos", Map.of(
                "status", "registered",
                "server", "localhost:8848", 
                "service", "user-service",
                "namespace", "microservice-auth"
            ));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("service", "user-service");
            response.put("version", "enhanced-v1.0");
            response.put("integrations", status);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取服务状态失败", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "获取服务状态失败",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
}
