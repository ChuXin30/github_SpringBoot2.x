# Nacos CORS 跨域问题解决方案

## 🔍 **问题分析**

您遇到的 CORS 问题表现为：
- 重复的 `access-control-allow-origin` 头
- 多个 `vary` 头导致浏览器缓存问题
- 跨域请求被浏览器阻止

## 🚀 **解决方案**

### **方案 1: 使用 Nginx 反向代理（推荐）**

#### **1. 安装 Nginx**
```bash
# macOS
brew install nginx

# 或者使用 Docker
docker run -d --name nginx-cors-proxy \
  -p 8849:80 \
  -v $(pwd)/nginx-cors-proxy.conf:/etc/nginx/conf.d/default.conf \
  nginx:alpine
```

#### **2. 配置 Nginx**
使用我们创建的 `nginx-cors-proxy.conf` 配置文件：

```nginx
server {
    listen 8849;
    server_name localhost;

    # 添加 CORS 头
    add_header 'Access-Control-Allow-Origin' '*' always;
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
    add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers' always;
    add_header 'Access-Control-Allow-Credentials' 'true' always;
    add_header 'Access-Control-Max-Age' '3600' always;

    location / {
        if ($request_method = 'OPTIONS') {
            add_header 'Access-Control-Allow-Origin' '*' always;
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
            add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers' always;
            add_header 'Access-Control-Allow-Credentials' 'true' always;
            add_header 'Access-Control-Max-Age' '3600' always;
            add_header 'Content-Type' 'text/plain; charset=utf-8';
            add_header 'Content-Length' 0;
            return 204;
        }

        # 代理到 Nacos
        proxy_pass http://localhost:8848;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### **3. 使用代理地址**
将您的应用中的 Nacos 地址从：
```
http://localhost:8848
```
改为：
```
http://localhost:8849
```

### **方案 2: 前端代理配置**

#### **Vue.js / React 开发环境**
```javascript
// vue.config.js 或 webpack.config.js
module.exports = {
  devServer: {
    proxy: {
      '/nacos': {
        target: 'http://localhost:8848',
        changeOrigin: true,
        pathRewrite: {
          '^/nacos': '/nacos'
        },
        onProxyRes: function(proxyRes, req, res) {
          // 添加 CORS 头
          proxyRes.headers['Access-Control-Allow-Origin'] = '*';
          proxyRes.headers['Access-Control-Allow-Methods'] = 'GET, POST, PUT, DELETE, OPTIONS';
          proxyRes.headers['Access-Control-Allow-Headers'] = 'Content-Type, Authorization, X-Requested-With, Accept, Origin';
          proxyRes.headers['Access-Control-Allow-Credentials'] = 'true';
        }
      }
    }
  }
};
```

### **方案 3: 浏览器扩展（临时解决）**

安装 CORS 浏览器扩展：
- Chrome: "CORS Unblock" 或 "Disable CORS"
- Firefox: "CORS Everywhere"

⚠️ **注意**: 仅用于开发环境，生产环境请使用其他方案。

### **方案 4: 修改 Nacos 配置**

#### **1. 创建自定义配置文件**
```properties
# nacos-custom.properties
nacos.security.ignore.urls=${NACOS_SECURITY_IGNORE_URLS:/,/error,/**/*.css,/**/*.js,/**/*.html,/**/*.map,/**/*.svg,/**/*.png,/**/*.ico,/console-fe/public/**,/v1/auth/**,/v1/console/health/**,/actuator/**,/v1/console/server/**}

# CORS 配置
nacos.cors.allowed.origins=http://127.0.0.1:8080,http://localhost:8080,http://127.0.0.1:3000,http://localhost:3000
nacos.cors.allowed.methods=GET,POST,PUT,DELETE,OPTIONS
nacos.cors.allowed.headers=Content-Type,Authorization,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers
nacos.cors.allow.credentials=true
nacos.cors.max.age=3600
```

#### **2. 更新 Docker Compose**
```yaml
nacos:
  image: nacos/nacos-server:v2.1.0
  container_name: nacos-server
  environment:
    MODE: standalone
    NACOS_AUTH_ENABLE: true
    # CORS 配置
    NACOS_CORS_ALLOWED_ORIGINS: "http://127.0.0.1:8080,http://localhost:8080,http://127.0.0.1:3000,http://localhost:3000"
    NACOS_CORS_ALLOWED_METHODS: "GET,POST,PUT,DELETE,OPTIONS"
    NACOS_CORS_ALLOWED_HEADERS: "Content-Type,Authorization,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers"
    NACOS_CORS_ALLOW_CREDENTIALS: "true"
    NACOS_CORS_MAX_AGE: "3600"
  ports:
    - "8848:8848"
    - "9848:9848"
  volumes:
    - nacos_data:/home/nacos/data
    - ./nacos-custom.properties:/home/nacos/conf/application-custom.properties
```

## 🧪 **测试 CORS 配置**

### **1. 测试预检请求**
```bash
curl -X OPTIONS "http://localhost:8848/nacos/v1/auth/login" \
  -H "Origin: http://127.0.0.1:8080" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v
```

### **2. 测试实际请求**
```bash
curl -X POST "http://localhost:8848/nacos/v1/auth/login" \
  -d "username=nacos&password=nacos" \
  -H "Origin: http://127.0.0.1:8080" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -v
```

### **3. 检查响应头**
确保响应包含正确的 CORS 头：
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With, Accept, Origin
Access-Control-Allow-Credentials: true
```

## 🔧 **Spring Boot 应用配置**

### **1. 添加 CORS 配置**
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
```

### **2. 配置 Nacos 客户端**
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # 或使用代理地址 localhost:8849
        username: nacos
        password: nacos
```

## 🎯 **最佳实践**

### **1. 生产环境**
- 使用 Nginx 反向代理
- 配置具体的允许域名，避免使用 `*`
- 启用 HTTPS

### **2. 开发环境**
- 使用前端代理配置
- 或使用 Nginx 代理

### **3. 安全考虑**
- 限制允许的域名
- 使用 HTTPS
- 定期更新 CORS 配置

## 🚨 **常见问题**

### **1. 仍然出现 CORS 错误**
- 检查浏览器控制台的具体错误信息
- 确认请求的 Origin 头是否正确
- 验证服务器返回的 CORS 头

### **2. 预检请求失败**
- 确保服务器正确处理 OPTIONS 请求
- 检查 Access-Control-Allow-Methods 头
- 验证 Access-Control-Allow-Headers 头

### **3. 凭证问题**
- 当使用 `Access-Control-Allow-Credentials: true` 时
- 不能使用 `Access-Control-Allow-Origin: *`
- 必须指定具体的域名

## 📝 **总结**

推荐使用 **Nginx 反向代理** 方案，因为：
1. ✅ 不需要修改 Nacos 源码
2. ✅ 配置简单，易于维护
3. ✅ 可以统一处理多个服务的 CORS 问题
4. ✅ 支持负载均衡和缓存
5. ✅ 生产环境友好

选择适合您环境的方案，解决 CORS 跨域问题！🚀
