# CORS 问题简单解决方案

## 问题分析

您遇到的 CORS 问题是由于 Nacos 返回了重复的 `access-control-allow-origin` 头部和多个 `vary` 头部：

```
access-control-allow-credentials: true
access-control-allow-origin: http://127.0.0.1:8080
access-control-allow-origin: *
```

## 解决方案

### 方案 1: 修改前端配置（推荐）

在前端应用中，将 Nacos 服务器地址改为使用 `localhost` 而不是 `127.0.0.1`：

```javascript
// 修改前
const nacosServer = 'http://127.0.0.1:8848/nacos';

// 修改后
const nacosServer = 'http://localhost:8848/nacos';
```

### 方案 2: 使用环境变量

在微服务配置中，确保使用一致的地址：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # 使用 localhost 而不是 127.0.0.1
```

### 方案 3: 浏览器扩展（临时解决）

安装 CORS 浏览器扩展，如 "CORS Unblock" 或 "Disable CORS"。

### 方案 4: 启动浏览器时禁用 CORS（开发环境）

```bash
# Chrome
open -n -a /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --args --user-data-dir="/tmp/chrome_dev_test" --disable-web-security --disable-features=VizDisplayCompositor

# Safari
# 在 Safari 开发菜单中启用 "Disable Cross-Origin Restrictions"
```

## 当前状态

✅ **已完成的修复**：
- 移除了 Docker Compose 中重复的 CORS 配置
- 简化了 nacos-custom.properties 中的 CORS 配置
- 重启了 Nacos 容器

✅ **服务状态**：
- Nacos: 运行中 (端口 8848)
- 网关服务: 运行中 (端口 8080)
- 用户服务: 运行中 (端口 8082)

## 建议操作

1. **立即解决**：在前端代码中将 `127.0.0.1` 改为 `localhost`
2. **长期解决**：统一使用 `localhost` 作为服务器地址
3. **验证修复**：重新测试登录功能

## 测试命令

```bash
# 测试 Nacos 连接
curl -s http://localhost:8848/nacos/ | grep -i "nacos"

# 测试认证
curl -s -X POST "http://localhost:8848/nacos/v1/auth/login" -d "username=nacos&password=nacos"

# 检查服务状态
./check-services-status.sh
```

这个简单的地址修改应该能解决您的 CORS 问题！
