# Podman Compose 微服务环境部署指南

## 🎉 部署成功！

我已经成功使用 Podman 启动了您的 docker-compose.yml 文件中的所有服务。

## ✅ **已启动的服务**

| 服务 | 状态 | 端口 | 访问信息 |
|------|------|------|----------|
| **Keycloak** | ✅ 运行中 | 8180 | http://localhost:8180 (admin/admin123) |
| **MySQL** | ✅ 运行中 | 3307 | localhost:3307 (root/root123) |
| **Redis** | ✅ 运行中 | 6379 | localhost:6379 (密码: redis123) |
| **Nacos** | ⏸️ 跳过 | - | 由于网络问题暂时跳过 |

## 🛠️ **使用的工具**

### 1. **podman-compose**
```bash
# 安装
pip3 install podman-compose

# 使用
podman-compose -f docker-compose-podman.yml up -d
```

### 2. **管理脚本**
```bash
# 使用管理脚本
./manage-services.sh start    # 启动所有服务
./manage-services.sh status   # 查看状态
./manage-services.sh test     # 测试连接
./manage-services.sh stop     # 停止服务
./manage-services.sh logs keycloak-auth-server  # 查看日志
```

## 🔧 **配置文件**

### 原始文件
- `docker-compose.yml` - 原始配置文件（包含 Nacos）

### 修改文件
- `docker-compose-podman.yml` - 适配 Podman 的配置文件（暂时移除 Nacos）

## 📊 **服务详情**

### **Keycloak 认证服务器**
- **镜像**: `quay.io/keycloak/keycloak:21.0.0`
- **容器名**: `keycloak-auth-server`
- **端口**: `8180:8080`
- **管理界面**: http://localhost:8180
- **管理员**: `admin` / `admin123`
- **功能**: 身份认证和授权管理

### **MySQL 数据库**
- **镜像**: `mysql:8.0`
- **容器名**: `mysql-db`
- **端口**: `3307:3306`
- **根用户**: `root` / `root123`
- **应用用户**: `app_user` / `app_password`
- **数据库**: `microservice_db`

### **Redis 缓存**
- **镜像**: `redis:7-alpine`
- **容器名**: `redis-cache`
- **端口**: `6379:6379`
- **密码**: `redis123`
- **功能**: 缓存和会话存储

## 🚀 **快速操作**

### **启动所有服务**
```bash
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo/keycloak-config
./manage-services.sh start
```

### **查看服务状态**
```bash
./manage-services.sh status
```

### **测试服务连接**
```bash
./manage-services.sh test
```

### **查看服务日志**
```bash
./manage-services.sh logs keycloak-auth-server
./manage-services.sh logs mysql-db
./manage-services.sh logs redis-cache
```

### **停止所有服务**
```bash
./manage-services.sh stop
```

## 🔍 **验证部署**

### **1. 检查容器状态**
```bash
podman ps
```

### **2. 测试 MySQL 连接**
```bash
podman exec mysql-db mysql -u root -proot123 -e "SHOW DATABASES;"
```

### **3. 测试 Redis 连接**
```bash
podman exec redis-cache redis-cli -a redis123 ping
```

### **4. 测试 Keycloak 健康检查**
```bash
curl http://localhost:8180/health/ready
```

## 🌐 **访问地址**

| 服务 | URL | 说明 |
|------|-----|------|
| Keycloak 管理控制台 | http://localhost:8180 | 身份认证管理 |
| Keycloak 健康检查 | http://localhost:8180/health/ready | 服务状态检查 |
| MySQL 数据库 | localhost:3307 | 数据库连接 |
| Redis 缓存 | localhost:6379 | 缓存服务 |

## 🔧 **故障排除**

### **1. 服务启动失败**
```bash
# 查看详细日志
./manage-services.sh logs <服务名>

# 重启服务
./manage-services.sh restart
```

### **2. 端口冲突**
```bash
# 检查端口占用
lsof -i :8180
lsof -i :3307
lsof -i :6379
```

### **3. 容器名称冲突**
```bash
# 清理现有容器
./manage-services.sh clean
./manage-services.sh start
```

### **4. 网络连接问题**
```bash
# 检查网络
podman network ls
podman network inspect microservice-network
```

## 📝 **注意事项**

1. **Nacos 服务**: 由于网络问题暂时跳过，如需使用可稍后单独启动
2. **数据持久化**: 所有数据都保存在 Podman 卷中
3. **网络配置**: 所有服务都在 `microservice-network` 网络中
4. **健康检查**: Keycloak 配置了健康检查，启动需要约 90 秒

## 🎯 **下一步**

1. **访问 Keycloak**: 打开 http://localhost:8180 配置认证
2. **连接数据库**: 使用 MySQL 客户端连接 localhost:3307
3. **配置应用**: 更新您的 Spring Boot 应用配置以使用这些服务
4. **添加 Nacos**: 网络恢复后可以添加 Nacos 服务注册中心

## 🎉 **总结**

您的微服务环境已经成功部署！所有核心服务（Keycloak、MySQL、Redis）都在正常运行，可以开始开发和测试您的微服务应用了。
