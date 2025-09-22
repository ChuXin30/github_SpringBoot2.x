# Spring Boot ActiveMQ 示例项目 (Chapter 13Mq)

本项目演示了如何在Spring Boot中集成和使用ActiveMQ消息中间件，包括队列和主题的使用。

## 项目结构

```
chapter13Mq/
├── src/main/java/org/example/chapter14/
│   ├── Chapter14Application.java          # 主启动类
│   ├── config/
│   │   └── ActiveMQConfig.java           # ActiveMQ配置类
│   ├── controller/
│   │   └── ActiveMQController.java       # ActiveMQ演示控制器
│   ├── model/
│   │   ├── UserMessage.java              # 用户消息模型
│   │   └── OrderMessage.java             # 订单消息模型
│   └── service/
│       ├── MessageProducerService.java   # 消息生产者服务
│       └── MessageConsumerService.java   # 消息消费者服务
├── src/main/resources/
│   └── application.yml                   # 应用配置文件
└── pom.xml                              # Maven配置文件
```

## 功能特性

### 1. 消息队列 (Queue)
- **用户消息队列**: 处理用户相关消息
- **订单消息队列**: 处理订单相关消息
- **邮件消息队列**: 处理邮件发送消息
- **通知消息队列**: 处理系统通知消息

### 2. 消息主题 (Topic)
- **新闻主题**: 发布新闻消息
- **天气主题**: 发布天气信息
- **股票主题**: 发布股票信息

### 3. 消息类型
- **用户消息**: 用户注册、登录等信息
- **订单消息**: 订单创建、支付、发货等信息
- **邮件消息**: 邮件发送通知
- **通知消息**: 系统通知
- **新闻消息**: 新闻发布
- **天气消息**: 天气信息发布
- **股票消息**: 股票信息发布

## API接口

### 1. 队列消息发送
```http
POST /chapter13Mq/api/activemq/send/user
POST /chapter13Mq/api/activemq/send/order
POST /chapter13Mq/api/activemq/send/email
POST /chapter13Mq/api/activemq/send/notification
```

### 2. 主题消息发布
```http
POST /chapter13Mq/api/activemq/publish/news
POST /chapter13Mq/api/activemq/publish/weather
POST /chapter13Mq/api/activemq/publish/stock
```

### 3. 其他功能
```http
POST /chapter13Mq/api/activemq/send/text          # 发送文本消息
POST /chapter13Mq/api/activemq/send/batch         # 批量发送消息
POST /chapter13Mq/api/activemq/send/sample        # 发送示例消息
GET  /chapter13Mq/api/activemq/statistics         # 获取消息统计
POST /chapter13Mq/api/activemq/reset              # 重置计数器
GET  /chapter13Mq/api/activemq/system-info        # 获取系统信息
```

## 运行方式

### 1. 启动ActiveMQ
```bash
# 下载并启动ActiveMQ
# 访问 http://localhost:8161/admin
# 用户名/密码: admin/admin
```

### 2. 启动应用
```bash
cd chapter13Mq
mvn spring-boot:run
```

### 3. 访问应用
- 应用地址: http://localhost:8080/chapter13Mq
- ActiveMQ控制台: http://localhost:8161/admin

## 配置说明

### 1. ActiveMQ连接配置
```yaml
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
    pool:
      enabled: true
      max-connections: 10
```

### 2. JMS配置
```yaml
spring:
  jms:
    enabled: true
    pub-sub-domain: false
    listener:
      acknowledge-mode: auto
      concurrency: 1-5
```

### 3. 队列和主题配置
```yaml
activemq:
  queue:
    user: user.queue
    order: order.queue
    email: email.queue
    notification: notification.queue
  topic:
    news: news.topic
    weather: weather.topic
    stock: stock.topic
```

## 核心概念

### 1. 消息队列 (Queue)
- **点对点模式**: 一个消息只能被一个消费者消费
- **持久化**: 消息持久化到磁盘，重启后仍然存在
- **可靠性**: 保证消息不丢失

### 2. 消息主题 (Topic)
- **发布/订阅模式**: 一个消息可以被多个消费者消费
- **实时性**: 只有在线订阅者能收到消息
- **广播**: 向所有订阅者广播消息

### 3. 消息转换器
- **MappingJackson2MessageConverter**: 将Java对象转换为JSON消息
- **类型安全**: 支持类型检查和转换
- **序列化**: 自动处理对象序列化

### 4. 连接池
- **PooledConnectionFactory**: 连接池管理
- **性能优化**: 减少连接创建和销毁开销
- **资源管理**: 合理管理连接资源

## 使用示例

### 1. 发送用户消息
```bash
curl -X POST http://localhost:8080/chapter13Mq/api/activemq/send/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "张三",
    "email": "zhangsan@example.com",
    "content": "欢迎注册！",
    "messageType": "WELCOME"
  }'
```

### 2. 发送订单消息
```bash
curl -X POST http://localhost:8080/chapter13Mq/api/activemq/send/order \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "ORD-123456",
    "userId": 1001,
    "username": "李四",
    "totalAmount": 299.99,
    "status": "PENDING"
  }'
```

### 3. 发布新闻消息
```bash
curl -X POST http://localhost:8080/chapter13Mq/api/activemq/publish/news \
  -d "title=科技新闻&content=最新科技动态&category=科技"
```

### 4. 批量发送消息
```bash
curl -X POST http://localhost:8080/chapter13Mq/api/activemq/send/batch?count=10
```

### 5. 发送示例消息
```bash
curl -X POST http://localhost:8080/chapter13Mq/api/activemq/send/sample
```

## 监控和管理

### 1. ActiveMQ Web控制台
- 访问地址: http://localhost:8161/admin
- 查看队列和主题状态
- 监控消息数量和处理情况
- 管理连接和会话

### 2. 应用监控
- 查看消息统计信息
- 监控消息处理性能
- 查看系统资源使用情况

### 3. 日志监控
- 消息发送和接收日志
- 错误和异常日志
- 性能监控日志

## 最佳实践

### 1. 消息设计
- 消息应该尽可能小
- 使用有意义的消息类型
- 包含必要的元数据
- 考虑消息的版本兼容性

### 2. 错误处理
- 实现消息重试机制
- 处理死信队列
- 记录错误日志
- 监控异常情况

### 3. 性能优化
- 使用连接池
- 合理设置并发数
- 优化消息大小
- 监控内存使用

### 4. 安全考虑
- 使用认证和授权
- 加密敏感消息
- 限制访问权限
- 审计日志记录

## 常见问题

### 1. 连接问题
- 检查ActiveMQ是否启动
- 验证连接配置
- 检查网络连接
- 查看防火墙设置

### 2. 消息丢失
- 检查消息持久化设置
- 验证消费者是否正常
- 查看错误日志
- 检查消息过期时间

### 3. 性能问题
- 调整连接池大小
- 优化消息大小
- 增加并发数
- 监控系统资源

### 4. 内存问题
- 检查消息堆积
- 调整JVM参数
- 优化消息处理
- 清理无用消息

## 扩展功能

### 1. 消息路由
- 实现消息路由规则
- 支持条件路由
- 动态路由配置
- 路由监控

### 2. 消息转换
- 支持多种消息格式
- 消息格式转换
- 数据映射
- 格式验证

### 3. 消息聚合
- 消息批量处理
- 消息聚合
- 批量确认
- 聚合监控

### 4. 消息追踪
- 消息链路追踪
- 消息状态跟踪
- 处理时间监控
- 异常追踪

## 总结

本项目提供了一个完整的Spring Boot ActiveMQ集成示例，涵盖了：

1. **基础配置**: 连接工厂、消息转换器、JMS模板
2. **消息发送**: 队列和主题消息发送
3. **消息接收**: 异步消息监听和处理
4. **监控管理**: 消息统计和系统监控
5. **最佳实践**: 错误处理、性能优化、安全考虑

通过这个项目，您可以学习到ActiveMQ的核心概念和使用方法，为实际项目开发提供参考。
