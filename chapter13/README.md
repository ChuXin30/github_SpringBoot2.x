# Spring Boot 多线程示例项目

本项目演示了在Spring Boot中如何使用多线程，包括异步任务、线程池配置、以及不同场景下的多线程应用。

## 项目结构

```
chapter13/
├── src/main/java/org/example/chapter13/
│   ├── Chapter13Application.java          # 主启动类
│   ├── config/
│   │   └── AsyncConfig.java              # 异步任务配置类
│   ├── controller/
│   │   └── MultithreadingController.java # 多线程演示控制器
│   └── service/
│       └── AsyncService.java             # 异步服务类
├── src/main/resources/
│   └── application.yml                   # 应用配置文件
└── pom.xml                              # Maven配置文件
```

## 功能特性

### 1. 线程池配置
- **默认线程池**: 用于一般的异步任务
- **I/O密集型线程池**: 适用于数据库操作、网络请求等I/O密集型任务
- **CPU密集型线程池**: 适用于计算密集型任务
- **定时任务线程池**: 用于定时任务执行

### 2. 异步任务类型
- **基本异步任务**: 使用`@Async`注解的简单异步方法
- **带返回值的异步任务**: 返回`CompletableFuture`的异步方法
- **I/O密集型任务**: 模拟数据库操作、网络请求等
- **CPU密集型任务**: 模拟计算密集型操作
- **批量异步任务**: 批量处理多个任务
- **混合任务**: 同时执行不同类型的任务

## API接口

### 1. 基本异步任务
```http
POST /chapter13/api/multithreading/basic-async?taskName=测试任务
```

### 2. 带返回值的异步任务
```http
POST /chapter13/api/multithreading/async-with-result?taskName=带返回值任务
```

### 3. I/O密集型任务
```http
POST /chapter13/api/multithreading/io-intensive?taskName=I/O密集型任务
```

### 4. CPU密集型任务
```http
POST /chapter13/api/multithreading/cpu-intensive?taskName=CPU密集型任务
```

### 5. 并发网络请求
```http
POST /chapter13/api/multithreading/concurrent-requests?requestCount=5
```

### 6. 并发数据库操作
```http
POST /chapter13/api/multithreading/concurrent-db-operations?operationCount=3
```

### 7. 批量异步任务
```http
POST /chapter13/api/multithreading/batch-tasks?taskCount=10
```

### 8. 混合任务类型
```http
POST /chapter13/api/multithreading/mixed-tasks
```

### 9. 获取系统信息
```http
GET /chapter13/api/multithreading/system-info
```

## 运行方式

### 1. 编译项目
```bash
cd chapter13
mvn clean compile
```

### 2. 运行应用
```bash
mvn spring-boot:run
```

### 3. 访问应用
- 应用地址: http://localhost:8080/chapter13
- 健康检查: http://localhost:8080/chapter13/actuator/health
- 系统信息: http://localhost:8080/chapter13/actuator/info

## 核心概念

### 1. @Async注解
`@Async`注解用于标记异步方法，Spring会自动为这些方法创建代理，使其在单独的线程中执行。

```java
@Async
public void basicAsyncTask(String taskName) {
    // 异步执行的代码
}
```

### 2. 线程池配置
通过`ThreadPoolTaskExecutor`配置不同类型的线程池：

```java
@Bean("taskExecutor")
public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(200);
    executor.setThreadNamePrefix("Async-");
    return executor;
}
```

### 3. CompletableFuture
用于处理异步任务的返回值：

```java
@Async
public CompletableFuture<String> asyncTaskWithResult(String taskName) {
    // 异步执行
    return CompletableFuture.completedFuture("结果");
}
```

### 4. 线程池选择策略
- **I/O密集型任务**: 线程数可以设置为CPU核心数的2-4倍
- **CPU密集型任务**: 线程数通常等于CPU核心数
- **混合型任务**: 根据具体场景选择合适的线程池

## 最佳实践

### 1. 线程池配置
- 合理设置核心线程数和最大线程数
- 根据任务类型选择合适的队列容量
- 设置合适的拒绝策略
- 配置线程池关闭时的等待时间

### 2. 异步方法设计
- 异步方法应该是public的
- 避免在同一个类中调用异步方法
- 合理处理异常情况
- 使用CompletableFuture处理返回值

### 3. 性能优化
- 根据任务类型选择合适的线程池
- 避免创建过多的线程
- 合理使用线程池的队列
- 监控线程池的使用情况

## 注意事项

1. **异步方法限制**: `@Async`注解只能用于public方法
2. **自调用问题**: 同一个类中的方法调用不会触发异步执行
3. **异常处理**: 异步方法中的异常需要特殊处理
4. **资源管理**: 合理管理线程池资源，避免内存泄漏
5. **监控**: 使用Spring Boot Actuator监控线程池状态

## 扩展功能

### 1. 定时任务
可以结合`@Scheduled`注解实现定时任务：

```java
@Scheduled(fixedRate = 5000)
@Async("scheduledTaskExecutor")
public void scheduledTask() {
    // 定时执行的异步任务
}
```

### 2. 事件驱动
可以结合Spring的事件机制实现异步事件处理：

```java
@EventListener
@Async
public void handleCustomEvent(CustomEvent event) {
    // 异步处理事件
}
```

### 3. 监控和指标
使用Micrometer收集线程池相关指标：

```java
@Bean
public MeterRegistry meterRegistry() {
    return new SimpleMeterRegistry();
}
```

## 总结

Spring Boot提供了强大的多线程支持，通过合理的配置和使用，可以显著提高应用的性能和响应能力。关键是要根据具体的业务场景选择合适的线程池配置和异步处理策略。
