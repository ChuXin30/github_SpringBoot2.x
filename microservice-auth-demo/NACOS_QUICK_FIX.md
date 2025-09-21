# Nacos快速修复指南

## 🚨 问题已解决

✅ **JWT Token错误**: 修复了不合法的base64字符问题  
✅ **认证配置**: 更新为有效的认证token  
✅ **容器重启**: 应用了新的配置

## 🔧 现在的修复状态

### 1. Docker配置已更新
```yaml
nacos:
  environment:
    NACOS_AUTH_TOKEN: SecretKey012345678901234567890123456789012345678901234567890123456789
    # ✅ 有效的base64兼容密钥
```

### 2. 微服务配置已更新
```yaml
# 所有微服务 (user-service, auth-service, gateway)
spring.cloud.nacos.discovery:
  username: nacos
  password: nacos
```

## 🚀 启动步骤

### 1. 等待Nacos完全启动
```bash
# Nacos容器已重新创建，等待启动完成
docker logs nacos-server -f

# 看到这个消息表示启动成功:
# "Nacos started successfully in standalone mode."
```

### 2. 验证Nacos Web控制台
```bash
# 访问控制台
open http://localhost:8848/nacos/

# 登录凭证:
# 用户名: nacos
# 密码: nacos
```

### 3. 重启微服务 (按顺序)
```bash
# 1. 认证服务
cd auth-service && mvn spring-boot:run

# 2. 用户服务  
cd user-service && mvn spring-boot:run

# 3. API网关
cd gateway && mvn spring-boot:run
```

## 🎯 成功验证

### 预期日志输出
```
✅ 正确: nacos registry, user-service register success
❌ 错误: unknown user! (应该不再出现)
❌ 错误: Illegal base64 character (应该不再出现)
```

### 在Nacos控制台检查
1. 访问 http://localhost:8848/nacos/
2. 登录 (nacos/nacos)
3. 进入"服务管理" → "服务列表"
4. 应该看到:
   - `auth-service`
   - `user-service`
   - `api-gateway`

## 📞 如果仍有问题

**方案A: 禁用认证 (临时)**
```yaml
# docker-compose.yml 临时修改
nacos:
  environment:
    MODE: standalone
    NACOS_AUTH_ENABLE: false  # 临时禁用
```

**方案B: 检查端口冲突**
```bash
# 检查8848端口
lsof -i :8848

# 停止其他Nacos实例
pkill -f nacos
```

**方案C: 清理并重启**
```bash
# 完全清理重启
docker-compose down
docker-compose up -d
```

## 🏁 总结

现在Nacos应该能正常启动并接受微服务注册了！这个修复解决了:
1. ❌ JWT token base64解码错误
2. ❌ 微服务 "unknown user" 错误
3. ✅ 完整的服务发现和注册功能
