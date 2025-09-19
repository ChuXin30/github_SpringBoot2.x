# Spring Security 示例项目

这是一个演示 Spring Security 基本功能的简单示例项目。

## 功能特性

- **用户认证**: 基于表单的登录认证
- **用户授权**: 基于角色的访问控制
- **内存用户存储**: 使用内存中的用户数据
- **密码加密**: BCrypt 密码编码器
- **自定义登录页面**: 美观的登录界面
- **API 端点保护**: REST API 的安全控制

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── org/example/chapter12/
│   │       ├── Chapter12Application.java          # 主应用程序类
│   │       ├── config/
│   │       │   └── SecurityConfig.java            # Spring Security 配置
│   │       └── controller/
│   │           ├── HomeController.java            # 主页控制器
│   │           └── ApiController.java             # API 控制器
│   └── resources/
│       ├── application.properties                 # 应用配置
│       └── templates/                             # Thymeleaf 模板
│           ├── index.html                         # 首页
│           ├── login.html                         # 登录页面
│           ├── dashboard.html                     # 仪表板
│           ├── user.html                          # 用户页面
│           ├── admin.html                         # 管理员页面
│           └── access-denied.html                 # 访问被拒绝页面
└── test/
    └── java/
        └── org/example/chapter12/
            ├── Chapter12ApplicationTests.java     # 应用测试
            └── controller/
                ├── HomeControllerTest.java        # 控制器测试
                └── ApiControllerTest.java         # API 测试
```

## 测试账户

项目提供了两个测试账户：

### 普通用户
- **用户名**: `user`
- **密码**: `password`
- **角色**: `USER`

### 管理员
- **用户名**: `admin`
- **密码**: `admin`
- **角色**: `ADMIN`

## 运行项目

1. **编译项目**:
   ```bash
   mvn clean compile
   ```

2. **运行测试**:
   ```bash
   mvn test
   ```

3. **启动应用**:
   ```bash
   mvn spring-boot:run
   ```

4. **访问应用**:
   打开浏览器访问 `http://localhost:8080`

## 页面说明

### 公开页面
- **首页** (`/`): 应用首页，显示登录状态和功能导航
- **登录页面** (`/login`): 用户登录界面

### 受保护页面
- **仪表板** (`/dashboard`): 需要认证，显示用户信息
- **用户页面** (`/user`): 需要认证，普通用户功能页面
- **管理员页面** (`/admin`): 需要 ADMIN 角色，管理员专用页面

### API 端点
- **公开 API** (`/api/public`): 无需认证
- **受保护 API** (`/api/protected`): 需要认证
- **管理员 API** (`/api/admin`): 需要 ADMIN 角色

## 安全配置说明

### 认证配置
- 使用内存用户存储
- BCrypt 密码编码
- 自定义登录页面
- 登录成功/失败处理

### 授权配置
- 基于角色的访问控制
- 路径级别的权限控制
- 自定义访问被拒绝页面

### 安全特性
- CSRF 保护（默认启用）
- 会话管理
- 安全头设置
- 异常处理

## 技术栈

- **Spring Boot 2.7.18**: 应用框架
- **Spring Security**: 安全框架
- **Thymeleaf**: 模板引擎
- **Bootstrap 5**: UI 框架
- **Maven**: 构建工具
- **JUnit 5**: 测试框架

## 学习要点

1. **Spring Security 配置**: 了解如何配置安全过滤器链
2. **用户认证**: 学习表单登录和用户详情服务
3. **用户授权**: 理解基于角色的访问控制
4. **密码安全**: 掌握密码编码和存储
5. **测试安全**: 学习如何测试安全功能

## 扩展建议

1. 集成数据库用户存储
2. 添加用户注册功能
3. 实现记住我功能
4. 添加 OAuth2 支持
5. 集成 JWT 令牌认证
6. 添加多因素认证
7. 实现细粒度权限控制
