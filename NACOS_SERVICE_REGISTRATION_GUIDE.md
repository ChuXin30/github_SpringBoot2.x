# Nacos 服务注册问题解决方案

## 🎉 **问题已解决！**

您的 Nacos 服务注册功能现在完全正常！经过测试，所有功能都已正常工作。

## ✅ **测试结果**

### **服务状态检查**
```bash
=== MySQL 测试 ===
✅ 连接成功，数据库 microservice_db 已创建

=== Redis 测试 ===
✅ 连接成功，返回 PONG

=== Keycloak 测试 ===
⏳ 健康检查通过，服务正常运行

=== Nacos 测试 ===
✅ Nacos Web 界面可访问
✅ Nacos 认证成功
✅ Nacos 服务注册功能正常
```

## 🔍 **问题诊断过程**

### **1. 检查容器状态**
```bash
podman ps | grep nacos
# 结果: 容器正在运行
```

### **2. 检查启动日志**
```bash
podman logs nacos-server | tail -30
# 关键信息: "Nacos started successfully in stand alone mode. use embedded storage"
```

### **3. 测试 Web 界面**
```bash
curl -s http://localhost:8848/nacos/ | grep -i "nacos"
# 结果: ✅ Web 界面可访问
```

### **4. 测试认证功能**
```bash
curl -s -X POST "http://localhost:8848/nacos/v1/auth/login" -d "username=nacos&password=nacos"
# 结果: ✅ 获得访问令牌
```

### **5. 测试服务注册**
```bash
# 注册测试服务
curl -s -X POST "http://localhost:8848/nacos/v1/ns/instance?serviceName=test-service&ip=127.0.0.1&port=8080&accessToken=$TOKEN"
# 结果: ✅ 返回 "ok"

# 验证服务注册
curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=test-service&accessToken=$TOKEN"
# 结果: ✅ 服务已成功注册
```

## 🚀 **Nacos 服务注册使用指南**

### **1. 获取访问令牌**
```bash
# 登录获取令牌
TOKEN=$(curl -s -X POST "http://localhost:8848/nacos/v1/auth/login" -d "username=nacos&password=nacos" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
echo "访问令牌: $TOKEN"
```

### **2. 注册服务**
```bash
# 注册服务实例
curl -X POST "http://localhost:8848/nacos/v1/ns/instance" \
  -d "serviceName=my-service" \
  -d "ip=127.0.0.1" \
  -d "port=8080" \
  -d "accessToken=$TOKEN"
```

### **3. 查询服务**
```bash
# 查询服务实例列表
curl "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=my-service&accessToken=$TOKEN"
```

### **4. 注销服务**
```bash
# 注销服务实例
curl -X DELETE "http://localhost:8848/nacos/v1/ns/instance" \
  -d "serviceName=my-service" \
  -d "ip=127.0.0.1" \
  -d "port=8080" \
  -d "accessToken=$TOKEN"
```

## 🔧 **Spring Boot 集成配置**

### **1. 添加依赖**
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    <version>2021.0.4.0</version>
</dependency>
```

### **2. 配置文件**
```yaml
spring:
  application:
    name: my-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        username: nacos
        password: nacos
        namespace: public
        group: DEFAULT_GROUP
```

### **3. 启用服务发现**
```java
@SpringBootApplication
@EnableDiscoveryClient
public class MyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyServiceApplication.class, args);
    }
}
```

## 🌐 **访问地址**

| 功能 | URL | 用户名/密码 | 说明 |
|------|-----|-------------|------|
| **Nacos 控制台** | http://localhost:8848/nacos | nacos/nacos | 服务管理界面 |
| **服务注册 API** | http://localhost:8848/nacos/v1/ns/instance | nacos/nacos | REST API |
| **服务发现 API** | http://localhost:8848/nacos/v1/ns/instance/list | nacos/nacos | 查询服务 |
| **配置管理 API** | http://localhost:8848/nacos/v1/cs/configs | nacos/nacos | 配置管理 |

## 🛠️ **管理命令**

### **使用管理脚本**
```bash
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo/keycloak-config

# 测试所有服务（包括 Nacos）
./manage-services.sh test

# 查看服务状态
./manage-services.sh status

# 查看 Nacos 日志
./manage-services.sh logs nacos-server
```

### **手动操作**
```bash
# 查看 Nacos 容器状态
podman ps | grep nacos

# 查看 Nacos 日志
podman logs nacos-server

# 重启 Nacos 服务
podman restart nacos-server

# 进入 Nacos 容器
podman exec -it nacos-server /bin/bash
```

## 🔍 **故障排除**

### **1. 服务注册失败**
```bash
# 检查认证
curl -X POST "http://localhost:8848/nacos/v1/auth/login" -d "username=nacos&password=nacos"

# 检查网络连接
curl -f http://localhost:8848/nacos/

# 检查服务状态
podman ps | grep nacos
```

### **2. 认证失败**
```bash
# 检查认证配置
podman exec nacos-server env | grep NACOS_AUTH

# 重置密码（如果需要）
# 访问 Web 界面: http://localhost:8848/nacos
# 用户名: nacos, 密码: nacos
```

### **3. 网络连接问题**
```bash
# 检查端口
lsof -i :8848
lsof -i :9848

# 检查防火墙
sudo ufw status
```

## 📊 **性能监控**

### **1. 查看服务统计**
```bash
# 访问 Nacos 控制台
open http://localhost:8848/nacos

# 查看服务列表
# 查看实例健康状态
# 查看配置管理
```

### **2. 监控指标**
- 服务注册数量
- 实例健康状态
- 配置变更历史
- 系统资源使用

## 🎯 **最佳实践**

### **1. 服务命名规范**
- 使用小写字母和连字符
- 避免使用特殊字符
- 保持名称简洁明了

### **2. 健康检查**
- 配置健康检查端点
- 设置合理的检查间隔
- 监控服务健康状态

### **3. 配置管理**
- 使用命名空间隔离环境
- 合理使用配置分组
- 定期备份重要配置

## 🎉 **总结**

✅ **您的 Nacos 服务注册功能完全正常！**

- ✅ Nacos 服务已成功启动
- ✅ Web 界面可正常访问
- ✅ 认证功能正常工作
- ✅ 服务注册功能已验证
- ✅ 服务发现功能正常

现在您可以：
1. 访问 http://localhost:8848/nacos 管理服务
2. 使用 API 进行服务注册和发现
3. 在 Spring Boot 应用中集成 Nacos
4. 开始构建您的微服务架构

🚀 **您的微服务基础设施已经完全就绪！**
