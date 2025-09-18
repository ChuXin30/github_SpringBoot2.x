package com.example.chapter11.controller;

import com.example.chapter11.model.ApiResponse;
import com.example.chapter11.model.User;
import com.example.chapter11.service.RestTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RestTemplate 演示控制器
 * 提供各种RestTemplate用法的API接口
 */
@RestController
@RequestMapping("/api/resttemplate")
public class RestTemplateController {

    @Autowired
    private RestTemplateService restTemplateService;

    /**
     * 获取单个用户
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        try {
            User user = restTemplateService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "获取用户失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = restTemplateService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "获取用户列表失败: " + e.getMessage()));
        }
    }

    /**
     * 使用ResponseEntity获取用户
     */
    @GetMapping("/user/{id}/response-entity")
    public ResponseEntity<User> getUserWithResponseEntity(@PathVariable Long id) {
        try {
            return restTemplateService.getUserWithResponseEntity(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 创建用户
     */
    @PostMapping("/user")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        try {
            User createdUser = restTemplateService.createUser(user);
            return ResponseEntity.ok(ApiResponse.success(createdUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "创建用户失败: " + e.getMessage()));
        }
    }

    /**
     * 使用ResponseEntity创建用户
     */
    @PostMapping("/user/response-entity")
    public ResponseEntity<User> createUserWithResponseEntity(@RequestBody User user) {
        try {
            return restTemplateService.createUserWithResponseEntity(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/user/{id}")
    public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            restTemplateService.updateUser(id, user);
            return ResponseEntity.ok(ApiResponse.success("更新用户成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "更新用户失败: " + e.getMessage()));
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        try {
            restTemplateService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("删除用户成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "删除用户失败: " + e.getMessage()));
        }
    }

    /**
     * 使用自定义HTTP头获取用户
     */
    @GetMapping("/user/{id}/custom-headers")
    public ResponseEntity<ApiResponse<User>> getUserWithCustomHeaders(@PathVariable Long id) {
        try {
            User user = restTemplateService.getUserWithCustomHeaders(id);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "获取用户失败: " + e.getMessage()));
        }
    }

    /**
     * 使用URL参数获取用户
     */
    @GetMapping("/users/params")
    public ResponseEntity<ApiResponse<List<User>>> getUsersWithParams(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age) {
        try {
            List<User> users = restTemplateService.getUsersWithParams(name, age);
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "获取用户失败: " + e.getMessage()));
        }
    }

    /**
     * 使用exchange方法获取用户
     */
    @GetMapping("/user/{id}/exchange")
    public ResponseEntity<ApiResponse<User>> getUserWithExchange(@PathVariable Long id) {
        try {
            ResponseEntity<ApiResponse<User>> response = restTemplateService.getUserWithExchange(id);
            if (response.getBody() != null && response.getBody().getData() != null) {
                return ResponseEntity.ok(ApiResponse.success(response.getBody().getData()));
            } else {
                // 如果exchange方法返回的不是ApiResponse格式，直接获取用户数据
                User user = restTemplateService.getUserById(id);
                return ResponseEntity.ok(ApiResponse.success(user));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "获取用户失败: " + e.getMessage()));
        }
    }

    /**
     * 带错误处理的获取用户
     */
    @GetMapping("/user/{id}/error-handling")
    public ResponseEntity<ApiResponse<User>> getUserWithErrorHandling(@PathVariable Long id) {
        try {
            User user = restTemplateService.getUserWithErrorHandling(id);
            if (user != null) {
                return ResponseEntity.ok(ApiResponse.success(user));
            } else {
                return ResponseEntity.ok(ApiResponse.error(404, "用户不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 使用简单RestTemplate获取数据
     */
    @GetMapping("/simple-data")
    public ResponseEntity<ApiResponse<String>> getSimpleData() {
        try {
            String data = restTemplateService.getSimpleData();
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "获取数据失败: " + e.getMessage()));
        }
    }

    /**
     * 获取RestTemplate使用说明
     */
    @GetMapping("/help")
    public ResponseEntity<ApiResponse<String>> getHelp() {
        String help = "RestTemplate 使用说明:\n\n" +
                "1. GET请求示例:\n" +
                "   GET /api/resttemplate/user/1\n" +
                "   GET /api/resttemplate/users\n\n" +
                "2. POST请求示例:\n" +
                "   POST /api/resttemplate/user\n" +
                "   Body: {\"name\":\"张三\",\"email\":\"zhangsan@example.com\",\"age\":25,\"address\":\"北京\"}\n\n" +
                "3. PUT请求示例:\n" +
                "   PUT /api/resttemplate/user/1\n" +
                "   Body: {\"name\":\"李四\",\"email\":\"lisi@example.com\",\"age\":30,\"address\":\"上海\"}\n\n" +
                "4. DELETE请求示例:\n" +
                "   DELETE /api/resttemplate/user/1\n\n" +
                "5. 其他功能:\n" +
                "   GET /api/resttemplate/user/1/custom-headers  - 自定义HTTP头\n" +
                "   GET /api/resttemplate/users/params?name=张三&age=25  - URL参数\n" +
                "   GET /api/resttemplate/user/1/exchange  - 使用exchange方法\n" +
                "   GET /api/resttemplate/user/1/error-handling  - 错误处理\n" +
                "   GET /api/resttemplate/simple-data  - 简单RestTemplate";
        
        return ResponseEntity.ok(ApiResponse.success(help));
    }
}
