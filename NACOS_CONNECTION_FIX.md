# Nacos 连接问题解决方案

## 🚨 **问题分析**

您遇到的错误：
```
com.alibaba.nacos.api.exception.NacosException: Client not connected, current status:STARTING
```

**根本原因**：
1. **Nacos 服务还在启动中** - 容器已运行但服务未完全就绪
2. **命名空间问题** - 应用配置使用了 `namespace: microservice-auth`，但该命名空间可能不存在
3. **连接超时** - Spring Boot 应用启动时 Nacos 还未准备好

## 🔧 **解决方案**

### **方案 1: 修改应用配置（推荐）**

#### **1. 临时使用默认命名空间**
修改所有微服务的 `application.yml`：

```yaml
# 修改前
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: microservice-auth  # ❌ 问题所在
        username: nacos
        password: nacos

# 修改后
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: public  # ✅ 使用默认命名空间
        username: nacos
        password: nacos
```

#### **2. 添加连接重试配置**
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: public
        username: nacos
        password: nacos
        # 添加重试配置
        retry:
          max-attempts: 10
          initial-interval: 1000
          max-interval: 2000
          multiplier: 1.5
        # 连接超时配置
        timeout: 30000
        # 心跳配置
        heartbeat-interval: 5000
        heartbeat-timeout: 15000
```

### **方案 2: 等待 Nacos 完全启动**

#### **1. 检查 Nacos 启动状态**
```bash
# 检查容器状态
podman ps | grep nacos

# 检查启动日志
podman logs nacos-server | grep -E "(started successfully|Nacos started)"

# 测试 Web 界面
curl -s http://localhost:8848/nacos/ | grep -i "nacos"
```

#### **2. 等待完全启动**
```bash
# 等待 Nacos 完全启动（通常需要 1-2 分钟）
sleep 60

# 验证服务可用
curl -X POST "http://localhost:8848/nacos/v1/auth/login" \
  -d "username=nacos&password=nacos"
```

### **方案 3: 创建命名空间**

#### **1. 通过 API 创建命名空间**
```bash
# 获取访问令牌
TOKEN=$(curl -s -X POST "http://localhost:8848/nacos/v1/auth/login" \
  -d "username=nacos&password=nacos" | \
  grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

# 创建命名空间
curl -X POST "http://localhost:8848/nacos/v1/console/namespaces" \
  -H "Authorization: Bearer $TOKEN" \
  -d "customNamespaceId=microservice-auth" \
  -d "namespaceName=microservice-auth" \
  -d "namespaceDesc=微服务认证命名空间"
```

#### **2. 通过 Web 界面创建**
1. 访问 http://localhost:8848/nacos
2. 用户名/密码: nacos/nacos
3. 进入 "命名空间" 管理
4. 创建新命名空间: `microservice-auth`

## 🚀 **立即修复步骤**

### **步骤 1: 修改配置文件**
```bash
# 修改 auth-service 配置
sed -i 's/namespace: microservice-auth/namespace: public/g' \
  microservice-auth-demo/auth-service/src/main/resources/application.yml

# 修改 gateway 配置
sed -i 's/namespace: microservice-auth/namespace: public/g' \
  microservice-auth-demo/gateway/src/main/resources/application.yml

# 修改 user-service 配置
sed -i 's/namespace: microservice-auth/namespace: public/g' \
  microservice-auth-demo/user-service/src/main/resources/application.yml
```

### **步骤 2: 重启应用**
```bash
# 按顺序重启微服务
# 1. 先启动 auth-service
cd microservice-auth-demo/auth-service
mvn spring-boot:run &

# 2. 等待 30 秒后启动 user-service
sleep 30
cd ../user-service
mvn spring-boot:run &

# 3. 等待 30 秒后启动 gateway
sleep 30
cd ../gateway
mvn spring-boot:run &
```

### **步骤 3: 验证注册**
```bash
# 检查服务注册状态
curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=auth-service" | \
  grep -o '"hosts":\[.*\]'

curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=user-service" | \
  grep -o '"hosts":\[.*\]'

curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=api-gateway" | \
  grep -o '"hosts":\[.*\]'
```

## 🔍 **故障排除**

### **1. 如果仍然连接失败**
```yaml
# 添加更详细的配置
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: public
        username: nacos
        password: nacos
        # 网络配置
        network-type: http
        # 日志配置
        log-name: nacos.log
        # 重试配置
        retry:
          max-attempts: 20
          initial-interval: 1000
          max-interval: 5000
          multiplier: 2.0
```

### **2. 检查网络连接**
```bash
# 测试端口连通性
telnet localhost 8848

# 测试 HTTP 连接
curl -v http://localhost:8848/nacos/

# 检查防火墙
sudo ufw status
```

### **3. 查看详细日志**
```yaml
# 在 application.yml 中添加
logging:
  level:
    com.alibaba.nacos: DEBUG
    com.alibaba.cloud.nacos: DEBUG
    org.springframework.cloud.client.discovery: DEBUG
```

## 📋 **配置模板**

### **完整的 Nacos 配置模板**
```yaml
spring:
  application:
    name: your-service-name
  
  cloud:
    nacos:
      discovery:
        # 基本配置
        server-addr: localhost:8848
        namespace: public
        group: DEFAULT_GROUP
        service: ${spring.application.name}
        cluster-name: default
        
        # 认证配置
        username: nacos
        password: nacos
        
        # 网络配置
        network-type: http
        timeout: 30000
        
        # 重试配置
        retry:
          max-attempts: 10
          initial-interval: 1000
          max-interval: 2000
          multiplier: 1.5
        
        # 心跳配置
        heartbeat-interval: 5000
        heartbeat-timeout: 15000
        
        # 元数据配置
        metadata:
          version: 1.0.0
          author: microservice-team
        
        # 注册配置
        register-enabled: true
        instance-enabled: true
        
        # 日志配置
        log-name: nacos.log
```

## 🎯 **总结**

**立即执行**：
1. ✅ 修改所有微服务的 `namespace: public`
2. ✅ 重启微服务应用
3. ✅ 验证服务注册成功

**长期优化**：
1. 🔄 等待 Nacos 完全启动后创建专用命名空间
2. 🔄 配置更完善的连接重试机制
3. 🔄 添加健康检查和监控

这样应该能解决您的 Nacos 连接问题！🚀
