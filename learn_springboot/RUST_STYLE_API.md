# Rust风格接口使用指南

本项目实现了一套Rust风格的Java接口，提供了类型安全的错误处理和函数式编程风格。

## 核心特性

### 1. Result<T, E> 类型
类似于Rust的Result类型，用于处理可能成功或失败的操作。

```java
// 创建成功结果
Result<String, AppError> success = Result.ok("操作成功");

// 创建错误结果
Result<String, AppError> error = Result.err(AppError.VALIDATION_ERROR);

// 链式操作
Result<String, AppError> result = someOperation()
    .map(value -> value.toUpperCase())
    .andThen(upperValue -> anotherOperation(upperValue));

// 模式匹配
String finalResult = result.match(
    success -> success,
    error -> "默认值"
);
```

### 2. Option<T> 类型
类似于Rust的Option类型，用于处理可能为空的值。

```java
// 创建有值Option
Option<String> some = Option.some("有值");

// 创建空值Option
Option<String> none = Option.none();

// 从可能为null的值创建
Option<String> fromNullable = Option.fromNullable(someValue);

// 链式操作
Option<String> result = fromNullable
    .map(value -> value.toUpperCase())
    .filter(value -> value.length() > 5);

// 模式匹配
String finalValue = result.match(
    value -> value,
    () -> "默认值"
);
```

### 3. AppError 错误类型
预定义的错误类型枚举，提供结构化的错误信息。

```java
// 创建错误
AppError error = AppError.VALIDATION_ERROR
    .withContext("field", "email")
    .withMessage("邮箱格式不正确");

// 从异常创建错误
AppError fromException = AppError.fromException(throwable);
```

## API接口

### 用户管理接口

#### 1. 获取用户信息
```http
GET /api/rust/user/{id}
```

**响应示例：**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "张三",
    "email": "zhangsan@example.com",
    "age": 25,
    "created_at": "2024-01-01T10:00:00",
    "updated_at": "2024-01-01T10:00:00"
  },
  "message": "用户信息获取成功"
}
```

#### 2. 创建用户
```http
POST /api/rust/user
Content-Type: application/json

{
  "name": "李四",
  "email": "lisi@example.com",
  "age": 30
}
```

#### 3. 更新用户
```http
PUT /api/rust/user/{id}
Content-Type: application/json

{
  "name": "李四",
  "email": "lisi@example.com",
  "age": 31
}
```

#### 4. 删除用户
```http
DELETE /api/rust/user/{id}
```

#### 5. 获取用户列表
```http
GET /api/rust/users?page=0&size=10
```

#### 6. 搜索用户
```http
GET /api/rust/users/search?name=张&email=example.com&page=0&size=10
```

#### 7. 批量创建用户
```http
POST /api/rust/users/batch
Content-Type: application/json

[
  {
    "name": "用户1",
    "email": "user1@example.com",
    "age": 25
  },
  {
    "name": "用户2",
    "email": "user2@example.com",
    "age": 30
  }
]
```

#### 8. 健康检查
```http
GET /api/rust/health
```

## 数据库表结构

### 用户表 (user)
```sql
CREATE TABLE `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
    `name` VARCHAR(100) NOT NULL COMMENT '用户姓名',
    `email` VARCHAR(255) NOT NULL COMMENT '用户邮箱',
    `age` INT(3) NULL DEFAULT NULL COMMENT '用户年龄',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_email` (`email`) COMMENT '邮箱唯一索引',
    KEY `idx_user_name` (`name`) COMMENT '姓名索引'
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='用户信息表';
```

## 错误处理

### 错误类型
- `VALIDATION_ERROR`: 数据验证失败
- `DATABASE_ERROR`: 数据库操作失败
- `RECORD_NOT_FOUND`: 记录未找到
- `DUPLICATE_RECORD`: 记录已存在
- `BUSINESS_ERROR`: 业务逻辑错误
- `INTERNAL_ERROR`: 内部服务器错误

### 错误响应格式
```json
{
  "success": false,
  "error": "VALIDATION_ERROR",
  "message": "数据验证失败",
  "context": {
    "field": "email",
    "detailed_message": "邮箱格式不正确"
  }
}
```

## 使用示例

### 1. 在服务层使用Result和Option
```java
@Service
public class UserService {
    
    public Result<User, AppError> createUser(User user) {
        return validateUser(user)
            .andThen(validatedUser -> {
                return checkEmailExists(validatedUser.getEmail())
                    .andThen(emailExists -> {
                        if (emailExists) {
                            return Result.err(AppError.DUPLICATE_RECORD);
                        }
                        return saveUser(validatedUser);
                    });
            });
    }
    
    public Option<User> getUserById(Long id) {
        User user = userRepository.findById(id);
        return Option.fromNullable(user);
    }
}
```

### 2. 在控制器中处理结果
```java
@RestController
public class UserController {
    
    @PostMapping("/user")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        return userService.createUser(user)
            .match(
                createdUser -> ResponseEntity.ok(createSuccessResponse(createdUser, "用户创建成功")),
                error -> ResponseEntity.badRequest().body(createErrorResponse(error))
            );
    }
}
```

## 优势

1. **类型安全**: 编译时就能发现错误处理的问题
2. **函数式编程**: 支持链式操作和函数组合
3. **明确的错误处理**: 不会忽略错误情况
4. **可读性强**: 代码意图更加明确
5. **可维护性**: 错误处理逻辑集中且一致

## 注意事项

1. 所有可能失败的操作都应该返回Result类型
2. 所有可能为空的值都应该使用Option类型
3. 错误信息应该包含足够的上下文信息
4. 使用match方法进行模式匹配，避免直接调用unwrap
