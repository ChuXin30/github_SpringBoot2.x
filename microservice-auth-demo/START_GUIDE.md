# 🚀 微服务认证架构 - 启动指南

这是一个完整的微服务认证架构项目启动指南，按照以下步骤操作即可快速运行整个系统。

## 📋 前置要求

请确保你的系统中已安装以下软件：

- ✅ **Docker** (>= 20.10.0)
- ✅ **Docker Compose** (>= 2.0.0)
- ✅ **Java 17** 或更高版本
- ✅ **Maven** (>= 3.6.0)
- ✅ **Git**

## 🎯 快速启动 (推荐)

### Step 1: 启动基础设施服务

```bash
# 进入项目根目录
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo

# 启动基础设施（Keycloak、MySQL、Redis、Nacos）
cd keycloak-config
docker-compose up -d

# 查看服务状态
docker-compose ps
```

**等待所有服务启动完成** (大约2-3分钟)

### Step 2: 验证基础设施

```bash
# 检查Keycloak是否启动成功
curl -f http://localhost:8180/auth/health || echo "Keycloak还在启动中..."

# 检查MySQL是否就绪
docker-compose logs mysql | grep "ready for connections"

# 检查Redis是否正常
docker exec redis-cache redis-cli ping
```

### Step 3: 访问Keycloak管理控制台

1. 浏览器访问：http://localhost:8180/auth/admin
2. 使用管理员账号登录：
   - **用户名**：`admin`
   - **密码**：`admin123`
3. 验证 `microservice-realm` 已自动导入
4. 检查预配置的用户和客户端

### Step 4: 启动微服务

**打开3个新的终端窗口，分别启动各个服务：**

**终端 1 - 认证服务:**
```bash
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo/auth-service
mvn spring-boot:run
# 等待看到 "Started AuthApplication" 日志
```

**终端 2 - 用户服务:**
```bash
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo/user-service
mvn spring-boot:run
# 等待看到 "Started UserApplication" 日志
```

**终端 3 - API网关:**
```bash
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo/gateway
mvn spring-boot:run
# 等待看到 "Started GatewayApplication" 日志
```

### Step 5: 验证系统运行

```bash
# 执行完整的API测试
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo
chmod +x test-api.sh
./test-api.sh
```

## 🌐 访问系统

启动成功后，你可以通过以下方式访问系统：

### 🎨 前端演示页面
- **URL**: http://localhost:8080 (通过网关访问)
- 或直接打开：`microservice-auth-demo/frontend-demo/index.html`
- **测试账号**：
  - 普通用户：`testuser` / `testpassword`
  - 管理员：`admin` / `adminpassword`

### 🔧 API接口

| 服务 | 端口 | 访问地址 | 说明 |
|------|------|----------|------|
| **API网关** | 8080 | http://localhost:8080 | 统一入口 |
| **认证服务** | 8081 | http://localhost:8081 | 直接访问 |
| **用户服务** | 8082 | http://localhost:8082 | 直接访问 |
| **Keycloak** | 8180 | http://localhost:8180 | 认证服务器 |

### 📊 管理控制台

| 服务 | 访问地址 | 账号信息 |
|------|----------|----------|
| **Keycloak管理** | http://localhost:8180/auth/admin | admin / admin123 |
| **Nacos控制台** | http://localhost:8848/nacos | nacos / nacos |
| **MySQL数据库** | localhost:3306 | root / root123 |

## 🧪 测试验证

### 1. 基础API测试

```bash
# 测试公开API（无需认证）
curl http://localhost:8080/api/user/public/info

# 测试登录
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpassword"}'

# 使用返回的token测试受保护API
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/user/profile
```

### 2. 权限测试

```bash
# 普通用户访问管理员API（应该被拒绝）
curl -H "Authorization: Bearer USER_TOKEN" \
  http://localhost:8080/api/user/list

# 管理员访问管理员API（应该成功）
curl -H "Authorization: Bearer ADMIN_TOKEN" \
  http://localhost:8080/api/user/list
```

### 3. 自动化测试

```bash
# 运行完整测试套件
./test-api.sh
```

## 📋 服务端口说明

| 端口 | 服务 | 状态检查 |
|------|------|----------|
| **8080** | API网关 | `curl http://localhost:8080/actuator/health` |
| **8081** | 认证服务 | `curl http://localhost:8081/actuator/health` |
| **8082** | 用户服务 | `curl http://localhost:8082/actuator/health` |
| **8180** | Keycloak | `curl http://localhost:8180/auth/health` |
| **8848** | Nacos | `curl http://localhost:8848/nacos` |
| **3306** | MySQL | `docker exec mysql-db mysqladmin ping` |
| **6379** | Redis | `docker exec redis-cache redis-cli ping` |

## ⚠️ 常见问题解决

### 1. 端口冲突

```bash
# 检查端口占用
lsof -i :8080
lsof -i :8180
lsof -i :3306

# 停止占用端口的进程
kill -9 <PID>
```

### 2. Docker服务启动失败

```bash
# 查看服务日志
docker-compose logs keycloak
docker-compose logs mysql

# 重启服务
docker-compose restart keycloak

# 完全重新创建
docker-compose down -v
docker-compose up -d
```

### 3. Keycloak导入失败

```bash
# 手动导入realm配置
docker cp realm-export.json keycloak-auth-server:/tmp/
docker exec keycloak-auth-server /opt/keycloak/bin/kc.sh import --file /tmp/realm-export.json
```

### 4. Maven依赖问题

```bash
# 清理并重新下载依赖
mvn clean compile -U
mvn dependency:resolve
```

### 5. 网络连接问题

```bash
# 检查Docker网络
docker network ls
docker network inspect microservice-network

# 重建网络
docker-compose down
docker-compose up -d
```

## 🔄 重启服务

### 重启基础设施
```bash
cd keycloak-config
docker-compose restart
```

### 重启单个微服务
```bash
# 在对应的服务目录中执行
mvn spring-boot:run
```

### 完全重置
```bash
# 停止所有服务
docker-compose down -v

# 删除数据卷（注意：会丢失数据）
docker volume prune

# 重新启动
docker-compose up -d
```

## 📈 性能监控

启动后可以通过以下端点监控系统状态：

- **网关监控**: http://localhost:8080/actuator/metrics
- **服务健康**: http://localhost:8080/actuator/health
- **Keycloak指标**: http://localhost:8180/auth/admin/master/console
- **数据库连接**: 通过Nacos控制台查看服务注册状态

## 🎉 启动成功标志

当你看到以下内容时，说明系统启动成功：

1. ✅ Docker容器都显示为 "Up" 状态
2. ✅ 各个Spring Boot服务显示 "Started Application" 日志
3. ✅ 测试脚本全部通过
4. ✅ 前端演示页面可以正常登录和调用API

**恭喜！你的微服务认证架构系统已经成功启动！🎊**
