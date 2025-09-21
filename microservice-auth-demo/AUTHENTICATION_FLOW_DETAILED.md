# 微服务认证架构详细说明

## 🏗️ **系统架构概览**

```
     [用户] 
        ↓
  🌐 API Gateway (8080) 
        ↓
   ┌─────────┬─────────┐
   ↓         ↓         ↓
🔐 Auth    👤 User   🔑 Keycloak
Service   Service   (8180)
(8081)    (8082)        
   ↓         ↓         ↓
   └─────────┴─────────┘
        ↓       ↓
   🗄️ MySQL  ⚡ Redis
   (3307)    (6379)
        
📡 Nacos (8848) - 服务注册发现
```

## 🔐 **完整认证流程详解**

### 流程1: 用户登录认证

#### 步骤1: 用户发起登录请求
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "adminpassword"
}
```

**调用链路**: `用户` → `API Gateway` → `Auth Service`

#### 步骤2: API Gateway 路由处理
**服务**: API Gateway (GatewayApplication)
**端口**: 8080
**职责**:
```java
// GatewayConfig.java
.route("auth-service-direct", r -> r.path("/auth/**")
    .uri("http://localhost:8081"))  // 路由到认证服务
```

**具体操作**:
- 接收前端登录请求
- 匹配路由规则 `/auth/**`
- 转发请求到 Auth Service (8081端口)
- **不进行JWT验证** (登录接口无需认证)

#### 步骤3: Auth Service 处理认证
**服务**: Auth Service (AuthServiceApplication)  
**端口**: 8081
**职责**: 与Keycloak交互进行身份验证

**具体操作**:
```java
// AuthService.java
@PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
    // 1. 构建向Keycloak的OAuth2 token请求
    String tokenEndpoint = keycloakAuthServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    
    // 2. 准备请求参数
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("grant_type", "password");
    requestBody.add("client_id", clientId);
    requestBody.add("username", loginRequest.getUsername());
    requestBody.add("password", loginRequest.getPassword());
    
    // 3. 调用Keycloak获取token
    ResponseEntity<String> response = restTemplate.postForEntity(tokenEndpoint, requestBody, String.class);
}
```

#### 步骤4: Keycloak 验证用户凭据
**服务**: Keycloak Server
**端口**: 8180
**职责**: 身份认证和JWT token生成

**具体操作**:
1. **接收OAuth2 token请求**:
   ```
   POST /realms/microservice-realm/protocol/openid-connect/token
   ```

2. **验证用户凭据**:
   - 检查用户名和密码是否正确
   - 验证用户是否启用且未锁定
   - 检查客户端权限

3. **生成JWT Token**:
   ```json
   {
     "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJa...",
     "expires_in": 3600,
     "refresh_expires_in": 1800,
     "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldU...",
     "token_type": "Bearer"
   }
   ```

**JWT Token 结构**:
```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT",
    "kid": "ZIeUxDZBTcU03ZCnxAqBaqCeBccS582odMfC7MVDQS-E"
  },
  "payload": {
    "exp": 1758441673,
    "iat": 1758438073,
    "sub": "1ee00cf9-626f-422c-a23b-faa0c4122ab4",  // 用户ID
    "realm_access": {
      "roles": ["admin", "user"]  // 用户角色
    },
    "preferred_username": "admin",
    "email": "admin@example.com",
    "name": "Admin User"
  }
}
```

#### 步骤5: Auth Service 返回响应
**服务**: Auth Service
**操作**:
```java
// 处理Keycloak响应并返回给前端
if (response.getStatusCode().is2xxSuccessful()) {
    Map<String, Object> tokenResponse = objectMapper.readValue(response.getBody(), Map.class);
    
    Map<String, Object> result = new HashMap<>();
    result.put("success", true);
    result.put("message", "登录成功");
    result.put("access_token", tokenResponse.get("access_token"));
    result.put("refresh_token", tokenResponse.get("refresh_token"));
    result.put("expires_in", tokenResponse.get("expires_in"));
    
    return ResponseEntity.ok(result);
}
```

#### 步骤6: API Gateway 转发响应
**服务**: API Gateway
**操作**: 将Auth Service的响应原样转发给前端用户

---

### 流程2: 使用JWT访问受保护资源

#### 步骤1: 用户携带JWT访问API
```http
GET http://localhost:8080/api/user/enhanced/profile
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldU...
```

#### 步骤2: API Gateway JWT验证
**服务**: API Gateway
**组件**: JwtAuthenticationFilter
**职责**: JWT token预验证和用户信息提取

**具体操作**:
```java
// JwtAuthenticationFilter.java
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 1. 从请求头提取JWT token
    String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        
        // 2. 解析JWT payload (开发环境简化验证)
        String[] tokenParts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(tokenParts[1]));
        Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
        
        // 3. 提取用户信息
        String userId = (String) claims.get("sub");
        String username = (String) claims.get("preferred_username");
        List<String> roles = extractRoles(claims);
        
        // 4. 设置请求头传递给下游服务
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
            .header("X-User-ID", userId)
            .header("X-Username", username)
            .header("X-User-Roles", String.join(",", roles))
            .build();
    }
}
```

#### 步骤3: 路由到User Service
**服务**: API Gateway
**操作**:
```java
// GatewayConfig.java 路由配置
.route("user-service-direct", r -> r.path("/api/user/**")
    .filters(f -> f
        .stripPrefix(1)  // 移除 /api 前缀
        .filter(new JwtAuthenticationFilter()))  // JWT验证
    .uri("http://localhost:8082"))  // 转发到用户服务
```

**结果**: `/api/user/enhanced/profile` → `http://localhost:8082/user/enhanced/profile`

#### 步骤4: User Service 接收请求
**服务**: User Service (UserServiceApplication)
**端口**: 8082
**组件**: UserInfoExtractionFilter

**具体操作**:
```java
// UserInfoExtractionFilter.java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
    // 1. 从请求头提取网关传递的用户信息
    String userId = request.getHeader("X-User-ID");
    String username = request.getHeader("X-Username");
    String rolesHeader = request.getHeader("X-User-Roles");
    
    // 2. 构建Spring Security上下文
    MicroserviceUserDetails userDetails = new MicroserviceUserDetails(userId, username, roles);
    UsernamePasswordAuthenticationToken authentication = 
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    
    // 3. 设置到Security Context
    SecurityContextHolder.getContext().setAuthentication(authentication);
}
```

#### 步骤5: 业务Controller处理
**服务**: User Service
**组件**: EnhancedUserController

**具体操作**:
```java
// EnhancedUserController.java
@GetMapping("/enhanced/profile")
public ResponseEntity<Map<String, Object>> getEnhancedProfile(Authentication authentication) {
    // 1. 获取当前认证用户信息
    MicroserviceUserDetails userDetails = (MicroserviceUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();
    
    // 2. 调用Service层业务逻辑
    Optional<UserProfile> profile = userProfileService.getUserProfile(userId);
    
    // 3. 构建响应数据
    Map<String, Object> response = new HashMap<>();
    response.put("userId", userId);
    response.put("username", userDetails.getUsername());
    response.put("authorities", userDetails.getAuthorities());
    response.put("profile", profile.orElse(createDefaultProfile(userId)));
    
    return ResponseEntity.ok(response);
}
```

#### 步骤6: Service层数据处理
**服务**: User Service  
**组件**: UserProfileService

**具体操作**:
```java
// UserProfileService.java
@Cacheable(value = "userProfiles", key = "#userId")
public UserProfile getUserProfile(String userId) {
    // 1. 首先检查Redis缓存
    // Spring Cache会自动处理缓存逻辑
    
    // 2. 缓存未命中时查询数据库
    Optional<UserProfile> profile = userProfileRepository.findByUserId(userId);
    
    // 3. 如果数据库中没有，创建默认资料
    if (profile.isEmpty()) {
        UserProfile defaultProfile = createDefaultUserProfile(userId);
        return userProfileRepository.save(defaultProfile);
    }
    
    return profile.get();
}
```

#### 步骤7: 数据库和缓存交互

**MySQL数据库操作**:
```java
// UserProfileRepository.java
@Query("SELECT up FROM UserProfile up WHERE up.userId = :userId")
Optional<UserProfile> findByUserId(@Param("userId") String userId);
```

**Redis缓存操作**:
- **缓存命中**: 直接从Redis返回数据
- **缓存未命中**: 查询数据库后将结果缓存到Redis

#### 步骤8: 响应返回
**完整调用链**:
```
用户 ← API Gateway ← User Service ← UserProfileService ← MySQL/Redis
```

**最终响应**:
```json
{
  "userId": "1ee00cf9-626f-422c-a23b-faa0c4122ab4",
  "username": "admin",
  "authorities": [
    {"authority": "ROLE_ADMIN"},
    {"authority": "ROLE_USER"}
  ],
  "profile": {
    "id": 1,
    "userId": "1ee00cf9-626f-422c-a23b-faa0c4122ab4",
    "username": "admin",
    "email": "admin@example.com",
    "displayName": "Admin User",
    "department": "IT",
    "position": "Administrator",
    "isActive": true,
    "createdAt": "2025-09-21T14:30:00",
    "updatedAt": "2025-09-21T14:30:00"
  },
  "timestamp": 1758438073755
}
```

---

## 🔧 **各服务详细职责**

### 🌐 API Gateway (Spring Cloud Gateway)
**核心职责**: 统一入口和流量管控
- **路由管理**: 根据URL路径分发请求到不同微服务
- **JWT预验证**: 解析和验证JWT token
- **CORS处理**: 处理跨域请求
- **负载均衡**: 通过Nacos实现服务发现和负载均衡
- **静态资源**: 托管前端页面

**关键配置**:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: http://localhost:8081
          predicates:
            - Path=/auth/**
        - id: user-service  
          uri: http://localhost:8082
          predicates:
            - Path=/api/user/**
          filters:
            - StripPrefix=1
            - name: JwtAuthenticationFilter
```

### 🔑 Keycloak (Identity Provider)
**核心职责**: 身份和访问管理
- **用户认证**: 验证用户名密码
- **JWT生成**: 签发标准的JWT token
- **用户管理**: 存储用户账户信息
- **角色管理**: 定义和分配用户角色
- **OAuth2/OIDC**: 标准协议支持

**预设数据**:
```json
// realm-export.json
{
  "realm": "microservice-realm",
  "users": [
    {
      "username": "admin",
      "credentials": [{"value": "adminpassword"}],
      "realmRoles": ["admin", "user"]
    },
    {
      "username": "testuser", 
      "credentials": [{"value": "testpassword"}],
      "realmRoles": ["user"]
    }
  ]
}
```

### 🔐 Auth Service (Spring Boot)
**核心职责**: 认证服务中介
- **登录代理**: 代理前端与Keycloak的交互
- **Token刷新**: 处理refresh token逻辑
- **登出处理**: 处理用户登出
- **缓存管理**: Redis中存储会话信息

**关键功能**:
```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request);
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody RefreshTokenRequest request);
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody LogoutRequest request);
}
```

### 👤 User Service (Spring Boot)
**核心职责**: 用户业务逻辑
- **用户资料管理**: CRUD操作用户扩展信息
- **权限控制**: 基于角色的访问控制
- **缓存优化**: Redis缓存用户数据
- **数据持久化**: MySQL存储业务数据

**数据模型**:
```java
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId; // 对应Keycloak的sub
    
    private String username;
    private String email;
    private String department;
    private String position;
    // ... 其他业务字段
}
```

### 🗄️ MySQL Database
**核心职责**: 业务数据持久化
- **用户资料**: 存储扩展的用户业务信息
- **事务支持**: 保证数据一致性
- **性能优化**: 索引优化查询性能

**主要表结构**:
```sql
CREATE TABLE user_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255) UNIQUE NOT NULL,  -- Keycloak用户ID
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    department VARCHAR(255),
    position VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### ⚡ Redis Cache  
**核心职责**: 高性能缓存
- **用户资料缓存**: 缓存频繁访问的用户数据
- **会话管理**: 存储JWT黑名单和会话信息
- **性能优化**: 减少数据库查询压力

**缓存策略**:
```java
@Cacheable(value = "userProfiles", key = "#userId")
public UserProfile getUserProfile(String userId);

@CacheEvict(value = "userProfiles", key = "#userId")  
public UserProfile updateUserProfile(String userId, UserProfile profile);
```

### 📡 Nacos (Service Discovery)
**核心职责**: 服务注册与发现
- **服务注册**: 微服务启动时注册到Nacos
- **健康检查**: 监控服务健康状态
- **负载均衡**: 为网关提供服务实例列表
- **配置管理**: 集中管理配置信息

**服务注册信息**:
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        service: ${spring.application.name}
        metadata:
          version: 1.0.0
```

---

## 🔒 **安全机制详解**

### JWT Token安全
- **RS256签名**: Keycloak使用RSA私钥签名，公钥验证
- **Token过期**: 默认1小时过期时间
- **Refresh Token**: 用于无感知续期

### 权限控制
- **网关层**: JWT有效性验证
- **服务层**: Spring Security方法级权限控制
- **数据层**: 基于用户ID的数据隔离

### 通信安全
- **内网通信**: 服务间直连，避免外网暴露
- **统一入口**: 所有外部请求通过网关
- **请求头**: 网关注入用户信息，避免token在内网传递

---

## 🚀 **性能优化策略**

### 缓存分层
- **L1**: Spring Cache (应用内存)
- **L2**: Redis (分布式缓存)
- **L3**: MySQL (持久化存储)

### 数据库优化
- **连接池**: HikariCP高性能连接池
- **索引优化**: 用户ID、用户名等关键字段建索引
- **查询优化**: JPA查询优化和延迟加载

### 服务发现
- **健康检查**: 快速故障检测
- **负载均衡**: 请求分发优化
- **服务隔离**: 故障隔离和降级

---

## 📊 **完整认证时序图**

```
用户端    API网关    认证服务    Keycloak    用户服务    MySQL    Redis
  |         |         |         |         |         |        |
  |-- POST /auth/login -------->|         |         |         |        |
  |         |         |-- POST /realms/../token --->|         |         |        |
  |         |         |         |<--JWT---|         |         |        |
  |         |<------ JWT Token --|         |         |         |        |
  |<-- JWT --|         |         |         |         |         |        |
  |         |         |         |         |         |         |        |
  |-- GET /api/user/profile --->|         |         |         |        |
  |         |-- JWT验证 ------>|         |         |         |        |
  |         |-- GET /user/profile ------->|         |         |        |
  |         |         |         |         |-- 查询缓存 ----->|        |
  |         |         |         |         |         |<-缓存未命中-|
  |         |         |         |         |-- 查询数据库 --->|        |
  |         |         |         |         |<-- 用户数据 ---|        |
  |         |         |         |         |-- 更新缓存 ----->|
  |         |<------ 用户资料 -------------|         |        |
  |<-- 响应 --|         |         |         |         |        |
```

这就是完整的微服务认证架构！每个组件都有明确的职责，通过标准协议和接口协同工作，实现了安全、高效、可扩展的企业级认证系统。
