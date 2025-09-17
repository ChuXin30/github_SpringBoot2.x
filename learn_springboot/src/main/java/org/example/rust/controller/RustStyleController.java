package org.example.rust.controller;

import org.example.rust.*;
import org.example.rust.model.User;
import org.example.rust.service.RustStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rust风格的控制器
 * 使用Result<T, AppError>和Option<T>进行错误处理
 */
@RestController
@RequestMapping("/api/rust")
public class RustStyleController {
    
    @Autowired
    private RustStyleService rustStyleService;
    
    /**
     * 创建成功响应的辅助方法
     */
    private Map<String, Object> createSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", message);
        return response;
    }
    
    /**
     * 创建错误响应的辅助方法
     */
    private Map<String, Object> createErrorResponse(AppError error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error.getCode());
        response.put("message", error.getMessage());
        response.put("context", error.getContext());
        return response;
    }
    
    /**
     * 获取用户信息 - 使用Option处理可能不存在的情况
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        return rustStyleService.getUserById(id)
            .match(
                user -> ResponseEntity.ok(createSuccessResponse(user, "用户信息获取成功")),
                () -> ResponseEntity.notFound().build()
            );
    }
    
    /**
     * 创建用户 - 使用Result处理可能失败的情况
     */
    @PostMapping("/user")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        return rustStyleService.createUser(user)
            .match(
                createdUser -> ResponseEntity.ok(createSuccessResponse(createdUser, "用户创建成功")),
                error -> ResponseEntity.badRequest().body(createErrorResponse(error))
            );
    }
    
    /**
     * 更新用户 - 链式操作示例
     */
    @PutMapping("/user/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id, 
            @RequestBody User user) {
        
        return rustStyleService.getUserById(id)
            .okOr(AppError.RECORD_NOT_FOUND.withContext("user_id", id))
            .andThen(existingUser -> rustStyleService.updateUser(id, user))
            .match(
                updatedUser -> ResponseEntity.ok(createSuccessResponse(updatedUser, "用户更新成功")),
                error -> ResponseEntity.badRequest().body(createErrorResponse(error))
            );
    }
    
    /**
     * 删除用户 - 使用Result处理删除操作
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        return rustStyleService.deleteUser(id)
            .match(
                success -> ResponseEntity.ok(createSuccessResponse(null, "用户删除成功")),
                error -> ResponseEntity.badRequest().body(createErrorResponse(error))
            );
    }
    
    /**
     * 获取用户列表 - 使用Result处理查询操作
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return rustStyleService.getUsers(page, size)
            .match(
                users -> ResponseEntity.ok(createSuccessResponse(users, "用户列表获取成功")),
                error -> ResponseEntity.internalServerError().body(createErrorResponse(error))
            );
    }
    
    /**
     * 搜索用户 - 复杂查询示例
     */
    @GetMapping("/users/search")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return rustStyleService.searchUsers(name, email, page, size)
            .match(
                users -> ResponseEntity.ok(createSuccessResponse(users, "用户搜索成功")),
                error -> ResponseEntity.badRequest().body(createErrorResponse(error))
            );
    }
    
    /**
     * 批量操作示例 - 使用Result处理批量操作
     */
    @PostMapping("/users/batch")
    public ResponseEntity<Map<String, Object>> batchCreateUsers(@RequestBody List<User> users) {
        return rustStyleService.batchCreateUsers(users)
            .match(
                results -> ResponseEntity.ok(createSuccessResponse(results, "批量创建用户完成")),
                error -> ResponseEntity.badRequest().body(createErrorResponse(error))
            );
    }
    
    /**
     * 健康检查 - 使用Result处理系统状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return rustStyleService.healthCheck()
            .match(
                status -> ResponseEntity.ok(createSuccessResponse(status, "系统运行正常")),
                error -> ResponseEntity.status(503).body(createErrorResponse(error))
            );
    }
}
