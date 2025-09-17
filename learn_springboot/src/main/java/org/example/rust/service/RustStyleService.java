package org.example.rust.service;

import org.example.rust.*;
import org.example.rust.mapper.UserMapper;
import org.example.rust.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Rust风格的服务层
 * 使用Result<T, AppError>和Option<T>进行错误处理
 * 集成真实的数据库操作
 */
@Service
public class RustStyleService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 根据ID获取用户 - 返回Option<User>
     */
    public Option<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            return Option.none();
        }
        
        try {
            User user = userMapper.selectById(id);
            return Option.fromNullable(user);
        } catch (Exception e) {
            return Option.none();
        }
    }
    
    /**
     * 创建用户 - 返回Result<User, AppError>
     */
    public Result<User, AppError> createUser(User user) {
        // 验证用户数据
        return validateUser(user)
            .andThen(validatedUser -> {
                // 检查邮箱是否已存在
                return checkEmailExists(validatedUser.getEmail())
                    .andThen(emailExists -> {
                        if (emailExists) {
                            return Result.err(AppError.DUPLICATE_RECORD
                                .withContext("email", validatedUser.getEmail())
                                .withMessage("邮箱已存在"));
                        }
                        
                        // 创建新用户
                        User newUser = validatedUser.toBuilder()
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                        
                        try {
                            int result = userMapper.insert(newUser);
                            if (result > 0) {
                                return Result.ok(newUser);
                            } else {
                                return Result.err(AppError.DATABASE_ERROR
                                    .withMessage("用户创建失败"));
                            }
                        } catch (Exception e) {
                            return Result.err(AppError.DATABASE_ERROR
                                .withException(e)
                                .withMessage("用户创建失败"));
                        }
                    });
            });
    }
    
    /**
     * 更新用户 - 返回Result<User, AppError>
     */
    public Result<User, AppError> updateUser(Long id, User user) {
        if (id == null || id <= 0) {
            return Result.err(AppError.VALIDATION_ERROR
                .withContext("user_id", id)
                .withMessage("无效的用户ID"));
        }
        
        return getUserById(id)
            .okOr(AppError.RECORD_NOT_FOUND
                .withContext("user_id", id)
                .withMessage("用户不存在"))
            .andThen(existingUser -> {
                // 验证更新数据
                return validateUser(user)
                    .andThen(validatedUser -> {
                        // 检查邮箱是否被其他用户使用
                        return checkEmailExistsForOtherUser(validatedUser.getEmail(), id)
                            .andThen(emailExists -> {
                                if (emailExists) {
                                    return Result.err(AppError.DUPLICATE_RECORD
                                        .withContext("email", validatedUser.getEmail())
                                        .withMessage("邮箱已被其他用户使用"));
                                }
                                
                                // 更新用户
                                User updatedUser = existingUser.toBuilder()
                                    .name(validatedUser.getName())
                                    .email(validatedUser.getEmail())
                                    .age(validatedUser.getAge())
                                    .updatedAt(LocalDateTime.now())
                                    .build();
                                
                                try {
                                    int result = userMapper.updateById(updatedUser);
                                    if (result > 0) {
                                        return Result.ok(updatedUser);
                                    } else {
                                        return Result.err(AppError.DATABASE_ERROR
                                            .withMessage("用户更新失败"));
                                    }
                                } catch (Exception e) {
                                    return Result.err(AppError.DATABASE_ERROR
                                        .withException(e)
                                        .withMessage("用户更新失败"));
                                }
                            });
                    });
            });
    }
    
    /**
     * 删除用户 - 返回Result<Boolean, AppError>
     */
    public Result<Boolean, AppError> deleteUser(Long id) {
        if (id == null || id <= 0) {
            return Result.err(AppError.VALIDATION_ERROR
                .withContext("user_id", id)
                .withMessage("无效的用户ID"));
        }
        
        return getUserById(id)
            .okOr(AppError.RECORD_NOT_FOUND
                .withContext("user_id", id)
                .withMessage("用户不存在"))
            .andThen(user -> {
                try {
                    int result = userMapper.deleteById(id);
                    if (result > 0) {
                        return Result.ok(true);
                    } else {
                        return Result.err(AppError.DATABASE_ERROR
                            .withMessage("用户删除失败"));
                    }
                } catch (Exception e) {
                    return Result.err(AppError.DATABASE_ERROR
                        .withException(e)
                        .withMessage("用户删除失败"));
                }
            });
    }
    
    /**
     * 获取用户列表 - 返回Result<List<User>, AppError>
     */
    public Result<List<User>, AppError> getUsers(int page, int size) {
        if (page < 0 || size <= 0 || size > 100) {
            return Result.err(AppError.VALIDATION_ERROR
                .withContext("page", page)
                .withContext("size", size)
                .withMessage("无效的分页参数"));
        }
        
        try {
            int offset = page * size;
            List<User> users = userMapper.selectPage(offset, size);
            return Result.ok(users);
            
        } catch (Exception e) {
            return Result.err(AppError.DATABASE_ERROR
                .withException(e)
                .withMessage("获取用户列表失败"));
        }
    }
    
    /**
     * 搜索用户 - 返回Result<List<User>, AppError>
     */
    public Result<List<User>, AppError> searchUsers(String name, String email, int page, int size) {
        if (page < 0 || size <= 0 || size > 100) {
            return Result.err(AppError.VALIDATION_ERROR
                .withContext("page", page)
                .withContext("size", size)
                .withMessage("无效的分页参数"));
        }
        
        try {
            int offset = page * size;
            List<User> users = userMapper.searchUsers(name, email, offset, size);
            return Result.ok(users);
            
        } catch (Exception e) {
            return Result.err(AppError.DATABASE_ERROR
                .withException(e)
                .withMessage("搜索用户失败"));
        }
    }
    
    /**
     * 批量创建用户 - 返回Result<List<User>, AppError>
     */
    public Result<List<User>, AppError> batchCreateUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            return Result.err(AppError.VALIDATION_ERROR
                .withMessage("用户列表不能为空"));
        }
        
        if (users.size() > 100) {
            return Result.err(AppError.VALIDATION_ERROR
                .withContext("batch_size", users.size())
                .withMessage("批量创建用户数量不能超过100"));
        }
        
        try {
            // 为所有用户设置时间戳
            List<User> usersWithTimestamp = users.stream()
                .map(user -> user.toBuilder()
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build())
                .collect(Collectors.toList());
            
            int result = userMapper.batchInsert(usersWithTimestamp);
            if (result > 0) {
                return Result.ok(usersWithTimestamp);
            } else {
                return Result.err(AppError.DATABASE_ERROR
                    .withMessage("批量创建用户失败"));
            }
        } catch (Exception e) {
            return Result.err(AppError.DATABASE_ERROR
                .withException(e)
                .withMessage("批量创建用户失败"));
        }
    }
    
    /**
     * 健康检查 - 返回Result<Map<String, Object>, AppError>
     */
    public Result<Map<String, Object>, AppError> healthCheck() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("database", "healthy");
            
            // 获取用户总数
            Long userCount = userMapper.countUsers();
            status.put("user_count", userCount != null ? userCount : 0);
            
            status.put("timestamp", LocalDateTime.now());
            status.put("memory_usage", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            
            return Result.ok(status);
            
        } catch (Exception e) {
            return Result.err(AppError.SERVICE_UNAVAILABLE
                .withException(e)
                .withMessage("健康检查失败"));
        }
    }
    
    // 私有辅助方法
    
    /**
     * 验证用户数据
     */
    private Result<User, AppError> validateUser(User user) {
        if (user == null) {
            return Result.err(AppError.VALIDATION_ERROR.withMessage("用户数据不能为空"));
        }
        
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return Result.err(AppError.VALIDATION_ERROR
                .withContext("field", "name")
                .withMessage("用户名不能为空"));
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return Result.err(AppError.VALIDATION_ERROR
                .withContext("field", "email")
                .withMessage("邮箱不能为空"));
        }
        
        if (!isValidEmail(user.getEmail())) {
            return Result.err(AppError.VALIDATION_ERROR
                .withContext("field", "email")
                .withContext("value", user.getEmail())
                .withMessage("邮箱格式不正确"));
        }
        
        if (user.getAge() != null && (user.getAge() < 0 || user.getAge() > 150)) {
            return Result.err(AppError.VALIDATION_ERROR
                .withContext("field", "age")
                .withContext("value", user.getAge())
                .withMessage("年龄必须在0-150之间"));
        }
        
        return Result.ok(user);
    }
    
    /**
     * 检查邮箱是否存在
     */
    private Result<Boolean, AppError> checkEmailExists(String email) {
        try {
            Boolean exists = userMapper.existsByEmail(email);
            return Result.ok(exists != null ? exists : false);
        } catch (Exception e) {
            return Result.err(AppError.DATABASE_ERROR
                .withException(e)
                .withMessage("检查邮箱是否存在失败"));
        }
    }
    
    /**
     * 检查邮箱是否被其他用户使用
     */
    private Result<Boolean, AppError> checkEmailExistsForOtherUser(String email, Long excludeId) {
        try {
            Boolean exists = userMapper.existsByEmailAndNotId(email, excludeId);
            return Result.ok(exists != null ? exists : false);
        } catch (Exception e) {
            return Result.err(AppError.DATABASE_ERROR
                .withException(e)
                .withMessage("检查邮箱是否被其他用户使用失败"));
        }
    }
    
    /**
     * 简单的邮箱格式验证
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
}
