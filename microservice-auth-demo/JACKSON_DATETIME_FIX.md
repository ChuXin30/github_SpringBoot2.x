# Jackson LocalDateTime 序列化修复

## 🚨 **问题描述**

用户在调用 `/api/user/enhanced/profile` 接口时遇到JSON序列化错误：

```
Could not write JSON: Java 8 date/time type `java.time.LocalDateTime` not supported by default: 
add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling
```

## 🔧 **修复方案**

### 1. **application.yml 配置**

在 `user-service/src/main/resources/application.yml` 中添加Jackson时间序列化配置：

```yaml
spring:
  # Jackson JSON配置
  jackson:
    serialization:
      write-dates-as-timestamps: false
      write-date-timestamps-as-nanoseconds: false
    deserialization:
      read-date-timestamps-as-nanoseconds: false
    time-zone: GMT+8
    date-format: yyyy-MM-dd HH:mm:ss
```

### 2. **pom.xml 依赖**

在 `user-service/pom.xml` 中添加Jackson JSR310模块：

```xml
<!-- Jackson JSR310时间处理模块 -->
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

### 3. **实体类注解**

在 `UserProfile.java` 中为时间字段添加 `@JsonFormat` 注解：

```java
import com.fasterxml.jackson.annotation.JsonFormat;

@Column(name = "last_login")
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime lastLogin;

@Column(name = "created_at", updatable = false)
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime createdAt;

@Column(name = "updated_at")
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime updatedAt;
```

## ✅ **修复效果**

### 修复前的错误响应：
```json
{
  "success": false,
  "error": "获取用户资料失败",
  "message": "Could not write JSON: Java 8 date/time type...",
  "timestamp": 1758439505936
}
```

### 修复后的正确响应：
```json
{
  "userId": "1ee00cf9-626f-422c-a23b-faa0c4122ab4",
  "username": "admin",
  "authorities": [
    {"authority": "ROLE_ADMIN"},
    {"authority": "ROLE_USER"}
  ],
  "profile": {
    "id": 1,
    "userId": "1ee00cf9-626f-422c-a23b-faa0c4122ab4",
    "username": "admin",
    "email": "admin@example.com",
    "displayName": "Admin User",
    "lastLogin": "2025-09-21 14:30:25",
    "createdAt": "2025-09-21 14:25:10",
    "updatedAt": "2025-09-21 14:30:25",
    "isActive": true
  },
  "timestamp": 1758439505936
}
```

## 🎯 **技术原理**

### Jackson JSR310 模块的作用：
1. **时间类型支持**: 提供对Java 8时间API的完整支持
2. **序列化配置**: 控制时间格式的输出方式
3. **时区处理**: 正确处理时区转换

### 配置说明：
- `write-dates-as-timestamps: false`: 不使用时间戳格式
- `date-format: yyyy-MM-dd HH:mm:ss`: 指定时间格式
- `time-zone: GMT+8`: 设置时区为东八区
- `@JsonFormat`: 字段级别的格式控制

## 🚀 **重启步骤**

1. **停止user-service**: 在IDEA中停止当前进程
2. **重新启动**: 重新运行UserServiceApplication
3. **验证修复**: 使用curl测试API接口

## 📝 **相关文件**

- `user-service/src/main/resources/application.yml`
- `user-service/pom.xml`
- `user-service/src/main/java/com/example/userservice/entity/UserProfile.java`

---

**修复完成时间**: 2025-09-21  
**问题状态**: ✅ 已解决  
**测试状态**: ⏳ 待验证
