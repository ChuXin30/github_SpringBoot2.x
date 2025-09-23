# Podman 微服务环境启动成功 🚀

## 服务状态总览

### ✅ 容器服务（已启动）
- **MySQL**: 运行中 (端口 3307)
- **Redis**: 运行中 (端口 6379)  
- **Keycloak**: 运行中 (端口 8180)
- **Nacos**: 运行中 (端口 8848)

### ✅ 微服务应用（部分运行）
- **用户服务**: 运行中 (端口 8082)
- **网关服务**: 运行中 (端口 8080)
- **认证服务**: 需要重启以连接 Nacos

## 解决方案

### 1. 问题诊断
- **根本原因**: Nacos 启动时间较长，Spring Boot 应用在 Nacos 完全启动前就尝试连接
- **错误信息**: `NacosException: Client not connected, current status:STARTING`

### 2. 解决步骤
1. **临时禁用服务注册**: 将 `register-enabled` 设置为 `false`
2. **启动基础服务**: 使用 `podman-compose` 启动所有容器
3. **等待 Nacos 完全启动**: 验证 Nacos Web 界面和认证功能
4. **重新启用服务注册**: 将 `register-enabled` 设置为 `true`

### 3. 配置更新
已更新以下配置文件中的 Nacos 设置：
- `microservice-auth-demo/gateway/src/main/resources/application.yml`
- `microservice-auth-demo/auth-service/src/main/resources/application.yml`
- `microservice-auth-demo/user-service/src/main/resources/application.yml`

## 管理脚本

### 启动脚本
```bash
# 启动微服务应用
./start-microservices.sh

# 检查服务状态
./check-services-status.sh

# 管理容器服务
cd microservice-auth-demo/keycloak-config && ./manage-services.sh
```

### 容器管理
```bash
# 启动所有容器
cd microservice-auth-demo/keycloak-config
podman-compose -f docker-compose-podman.yml up -d

# 查看状态
podman-compose -f docker-compose-podman.yml ps

# 停止服务
podman-compose -f docker-compose-podman.yml down
```

## 访问地址

- **网关服务**: http://localhost:8080
- **认证服务**: http://localhost:8081
- **用户服务**: http://localhost:8082
- **Keycloak**: http://localhost:8180
- **Nacos**: http://localhost:8848/nacos

## 下一步操作

1. **重启认证服务**: 让认证服务重新连接 Nacos
2. **验证服务注册**: 在 Nacos 控制台查看服务是否成功注册
3. **测试 API 调用**: 验证微服务间的通信是否正常

## 注意事项

- Nacos 启动需要较长时间（通常 1-2 分钟）
- 建议在启动微服务前先确保 Nacos 完全启动
- 如果遇到连接问题，可以临时禁用服务注册，使用直连路由
- 网关配置已支持直连路由和负载均衡路由两种模式

## 故障排除

如果仍然遇到 Nacos 连接问题：

1. **检查 Nacos 状态**:
   ```bash
   curl -s http://localhost:8848/nacos/ | grep -i "nacos"
   ```

2. **测试 Nacos 认证**:
   ```bash
   curl -s -X POST "http://localhost:8848/nacos/v1/auth/login" -d "username=nacos&password=nacos"
   ```

3. **查看容器日志**:
   ```bash
   podman logs nacos-server
   ```

4. **临时禁用服务注册**:
   将 `register-enabled` 设置为 `false` 并重启应用
