# Redis Jackson LocalDateTime 序列化最终修复

## 🚨 **问题分析**

### 错误现象
用户调用 `/api/user/enhanced/profile` 接口时，出现Redis序列化错误：

```
org.springframework.data.redis.serializer.SerializationException: 
Could not write JSON: Java 8 date/time type `java.time.LocalDateTime` not supported by default
```

### 问题根源
1. **application.yml配置有限**: 只影响HTTP请求/响应序列化
2. **Redis独立序列化**: `GenericJackson2JsonRedisSerializer`使用独立的`ObjectMapper`
3. **缺少JSR310模块**: Redis序列化器没有`JavaTimeModule`支持
4. **缓存序列化失败**: `UserProfile`的`LocalDateTime`字段无法存储到Redis

### 调用链分析
```
✅ 用户认证通过 → ✅ 权限验证成功 → ✅ 数据库查询完成 
→ ❌ Redis缓存失败 (LocalDateTime序列化异常)
```

## 🔧 **最终修复方案**

### 修改文件: `CacheConfig.java`

**修复前** (问题代码):
```java
@Bean
public RedisCacheConfiguration cacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer())); // 问题所在
}
```

**修复后** (完整解决方案):
```java
@Bean
public RedisCacheConfiguration cacheConfiguration() {
    // 创建配置了JSR310的ObjectMapper
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());          // 支持Java 8时间API
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 字符串格式
    objectMapper.activateDefaultTyping(                        // 类型信息处理
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY
    );
    
    // 创建支持LocalDateTime的Redis序列化器
    GenericJackson2JsonRedisSerializer serializer = 
        new GenericJackson2JsonRedisSerializer(objectMapper);
    
    return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(serializer));                   // 使用配置的序列化器
}
```

## ✅ **修复效果**

### 修复前的错误日志:
```
ERROR --- SerializationException: Could not write JSON: 
Java 8 date/time type `java.time.LocalDateTime` not supported by default
```

### 修复后的成功流程:
```
INFO --- 已设置用户认证信息: admin, 角色: [admin, user]
DEBUG --- Authorized ReflectiveMethodInvocation: getEnhancedProfile
INFO --- 获取增强用户资料: 1ee00cf9-626f-422c-a23b-faa0c4122ab4
DEBUG --- 从数据库查询用户资料: 1ee00cf9-626f-422c-a23b-faa0c4122ab4
DEBUG --- Hibernate SQL: SELECT ... FROM user_profiles WHERE user_id=?
INFO --- Redis缓存成功存储
DEBUG --- 返回用户资料JSON
```

### API响应结果:
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
    "department": "IT部门",
    "position": "系统管理员",
    "lastLogin": "2025-09-21 15:35:39",
    "createdAt": "2025-09-21 14:25:00",
    "updatedAt": "2025-09-21 15:35:39",
    "isActive": true
  },
  "timestamp": 1758439539206
}
```

## 🎯 **技术原理**

### Jackson模块配置
1. **JavaTimeModule**: 提供Java 8时间API支持
2. **WRITE_DATES_AS_TIMESTAMPS=false**: 时间以字符串格式序列化
3. **DefaultTyping**: 保存类型信息，支持多态反序列化

### Redis序列化策略
- **Key序列化**: `StringRedisSerializer` (字符串格式)
- **Value序列化**: 配置了JSR310的`GenericJackson2JsonRedisSerializer`

### 缓存生命周期
- **TTL**: 10分钟自动过期
- **Null值**: 不缓存null值
- **更新策略**: `@CacheEvict`自动清理

## 🚀 **重启验证步骤**

1. **停止服务**: 停止UserServiceApplication
2. **重新启动**: 重新运行，加载新的CacheConfig
3. **API测试**: 调用`/api/user/enhanced/profile`接口
4. **Redis验证**: 检查Redis中是否成功存储用户数据

```bash
# 验证Redis中的缓存数据
docker exec -it microservice_redis redis-cli
> KEYS userProfiles::*
> GET "userProfiles::1ee00cf9-626f-422c-a23b-faa0c4122ab4"
```

## 📚 **相关文件**

- `user-service/src/main/java/com/example/userservice/config/CacheConfig.java`
- `user-service/src/main/java/com/example/userservice/entity/UserProfile.java`
- `user-service/src/main/java/com/example/userservice/service/UserProfileService.java`

---

**修复状态**: ✅ 完成  
**测试状态**: ⏳ 待用户重启验证  
**影响范围**: Redis缓存序列化，LocalDateTime字段支持

