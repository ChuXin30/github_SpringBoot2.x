# Chapter 11 - Spring Boot 示例项目

这是一个简单的 Spring Boot 2.7.18 示例项目，展示了基本的 REST API 开发。

## 项目结构

```
chapter11/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/chapter11/
│   │   │       ├── Chapter11Application.java    # 主应用程序类
│   │   │       └── controller/
│   │   │           └── HelloController.java     # REST 控制器
│   │   └── resources/
│   │       └── application.properties           # 应用配置
│   └── test/
│       └── java/
│           └── com/example/chapter11/
│               ├── Chapter11ApplicationTests.java
│               └── controller/
│                   └── HelloControllerTest.java
├── pom.xml                                      # Maven 配置
└── README.md                                    # 项目说明
```

## 功能特性

- **Spring Boot 2.7.18** - 使用稳定的 Spring Boot 版本
- **REST API** - 提供简单的 REST 接口
- **单元测试** - 包含完整的测试用例
- **JSON 响应** - 返回结构化的 JSON 数据

## API 接口

### 1. 简单问候接口
```
GET /api/hello
```

响应示例：
```json
{
  "message": "Hello, Spring Boot!",
  "timestamp": "2025-09-17T09:19:07.491105",
  "status": "success"
}
```

### 2. 带参数的问候接口
```
GET /api/hello/{name}
```

响应示例：
```json
{
  "message": "Hello, 张三!",
  "timestamp": "2025-09-17T09:19:09.392046",
  "status": "success"
}
```

### 3. 健康检查接口
```
GET /api/health
```

响应示例：
```json
{
  "service": "Chapter11 Demo",
  "status": "UP",
  "timestamp": "2025-09-17T09:19:11.074867"
}
```

## 如何运行

### 1. 编译项目
```bash
mvn clean compile
```

### 2. 运行测试
```bash
mvn test
```

### 3. 启动应用
```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

### 4. 测试 API
```bash
# 测试简单问候接口
curl http://localhost:8080/api/hello

# 测试带参数的问候接口
curl http://localhost:8080/api/hello/张三

# 测试健康检查接口
curl http://localhost:8080/api/health
```

## 技术栈

- **Java 8+**
- **Spring Boot 2.7.18**
- **Spring Web**
- **Maven 3.x**
- **JUnit 5** (测试)

## 配置说明

应用配置文件 `application.properties` 包含以下配置：

- 服务器端口：8080
- 应用名称：chapter11-demo
- 日志级别：INFO
- 激活配置文件：dev

## 开发说明

这个项目展示了 Spring Boot 的基本用法：

1. **自动配置** - Spring Boot 自动配置 Web 服务器和 Spring MVC
2. **REST 控制器** - 使用 `@RestController` 注解创建 REST API
3. **JSON 序列化** - 自动将 Java 对象序列化为 JSON
4. **测试支持** - 使用 `@WebMvcTest` 进行 Web 层测试
5. **Maven 集成** - 使用 Spring Boot Maven 插件运行应用

## 扩展建议

可以基于这个项目继续添加：

- 数据库集成 (JPA/Hibernate)
- 安全认证 (Spring Security)
- 数据验证
- 异常处理
- 日志记录
- 配置管理
- 监控和健康检查
