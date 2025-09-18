# RestTemplate 使用示例

这个项目演示了Spring Boot中RestTemplate的各种用法，包括GET、POST、PUT、DELETE请求，以及错误处理、自定义HTTP头等功能。

## 📋 项目结构

```
chapter11/
├── src/main/java/com/example/chapter11/
│   ├── config/
│   │   ├── RestTemplateConfig.java          # RestTemplate配置类
│   │   └── LoggingRequestInterceptor.java   # 请求日志拦截器
│   ├── controller/
│   │   └── RestTemplateController.java      # RestTemplate演示控制器
│   ├── model/
│   │   ├── User.java                        # 用户实体类
│   │   └── ApiResponse.java                 # API响应包装类
│   ├── service/
│   │   └── RestTemplateService.java         # RestTemplate服务类
│   └── Chapter11Application.java            # 主启动类
├── src/test/java/com/example/chapter11/
│   ├── controller/
│   │   └── RestTemplateControllerTest.java  # 控制器测试
│   └── service/
│       └── RestTemplateServiceTest.java     # 服务测试
└── pom.xml                                  # Maven配置
```

## 🚀 快速开始

### 1. 启动应用

```bash
mvn spring-boot:run
```

### 2. 访问API文档

启动后访问：`http://localhost:8080/api/resttemplate/help`

## 📚 RestTemplate 功能演示

### 1. 基本HTTP方法

#### GET请求
```bash
# 获取单个用户
GET http://localhost:8080/api/resttemplate/user/1

# 获取所有用户
GET http://localhost:8080/api/resttemplate/users
```

#### POST请求
```bash
# 创建用户
POST http://localhost:8080/api/resttemplate/user
Content-Type: application/json

{
    "name": "张三",
    "email": "zhangsan@example.com",
    "age": 25,
    "address": "北京"
}
```

#### PUT请求
```bash
# 更新用户
PUT http://localhost:8080/api/resttemplate/user/1
Content-Type: application/json

{
    "name": "李四",
    "email": "lisi@example.com",
    "age": 30,
    "address": "上海"
}
```

#### DELETE请求
```bash
# 删除用户
DELETE http://localhost:8080/api/resttemplate/user/1
```

### 2. 高级功能

#### 自定义HTTP头
```bash
GET http://localhost:8080/api/resttemplate/user/1/custom-headers
```

#### URL参数
```bash
GET http://localhost:8080/api/resttemplate/users/params?name=张三&age=25
```

#### 使用ResponseEntity
```bash
GET http://localhost:8080/api/resttemplate/user/1/response-entity
```

#### 使用exchange方法
```bash
GET http://localhost:8080/api/resttemplate/user/1/exchange
```

#### 错误处理
```bash
GET http://localhost:8080/api/resttemplate/user/999/error-handling
```

## 🔧 配置说明

### RestTemplate配置

```java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        // 配置连接池、超时时间等
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(20)
                .build();

        HttpComponentsClientHttpRequestFactory factory = 
                new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
        
        return restTemplate;
    }
}
```

### 请求日志拦截器

自动记录所有HTTP请求和响应的详细信息，包括：
- 请求方法、URL、头部信息
- 请求体内容
- 响应状态码、头部信息
- 响应时间

## 🧪 测试

### 运行所有测试
```bash
mvn test
```

### 运行特定测试类
```bash
mvn test -Dtest=RestTemplateServiceTest
mvn test -Dtest=RestTemplateControllerTest
```

## 📖 核心代码示例

### 1. 基本GET请求
```java
public User getUserById(Long id) {
    String url = BASE_URL + "/users/" + id;
    return restTemplate.getForObject(url, User.class);
}
```

### 2. POST请求
```java
public User createUser(User user) {
    String url = BASE_URL + "/users";
    return restTemplate.postForObject(url, user, User.class);
}
```

### 3. 使用自定义HTTP头
```java
public User getUserWithCustomHeaders(Long id) {
    String url = BASE_URL + "/users/" + id;
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("User-Agent", "RestTemplate-Demo/1.0");
    
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<User> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, User.class);
    
    return response.getBody();
}
```

### 4. 错误处理
```java
public User getUserWithErrorHandling(Long id) {
    try {
        return restTemplate.getForObject(url, User.class);
    } catch (Exception e) {
        if (e.getMessage().contains("404")) {
            return null; // 用户不存在
        } else if (e.getMessage().contains("500")) {
            throw new RuntimeException("服务器暂时不可用");
        } else {
            throw new RuntimeException("请求失败: " + e.getMessage());
        }
    }
}
```

## ⚠️ 注意事项

1. **RestTemplate已过时**：Spring 5.0+推荐使用WebClient
2. **同步阻塞**：RestTemplate是同步的，会阻塞线程
3. **错误处理**：需要手动处理各种HTTP错误状态
4. **超时配置**：建议配置合理的连接和读取超时时间

## 🔄 迁移到WebClient

如果需要使用异步非阻塞的HTTP客户端，可以考虑迁移到WebClient：

```java
@Bean
public WebClient webClient() {
    return WebClient.builder()
            .baseUrl("https://api.example.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
}

// 使用示例
public Mono<User> getUserAsync(Long id) {
    return webClient.get()
            .uri("/users/{id}", id)
            .retrieve()
            .bodyToMono(User.class);
}
```

## 📝 总结

这个项目全面演示了RestTemplate的各种用法，包括：
- 基本HTTP方法（GET、POST、PUT、DELETE）
- 自定义HTTP头和参数
- 错误处理机制
- 请求日志记录
- 连接池配置
- 单元测试

虽然RestTemplate已被标记为过时，但它仍然是学习HTTP客户端编程的好例子，并且在一些遗留项目中仍在使用。
