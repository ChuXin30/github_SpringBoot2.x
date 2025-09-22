# Spring Boot 多线程示例项目测试指南

本文档详细介绍了如何运行和测试Spring Boot多线程示例项目的各种测试。

## 测试结构

项目包含以下类型的测试：

### 1. 单元测试 (Unit Tests)
- **AsyncServiceTest**: 测试异步服务的各种功能
- **MultithreadingControllerTest**: 测试控制器的API接口

### 2. 集成测试 (Integration Tests)
- **MultithreadingIntegrationTest**: 测试完整的应用功能

### 3. 性能测试 (Performance Tests)
- **MultithreadingPerformanceTest**: 测试多线程性能表现

## 运行测试

### 方式一：使用测试脚本（推荐）

```bash
# 运行所有测试
./run-tests.sh

# 或者分别运行不同类型的测试
mvn test -Dtest="*Test"                    # 单元测试
mvn test -Dtest="*IntegrationTest"         # 集成测试
mvn test -Dtest="*PerformanceTest"         # 性能测试
```

### 方式二：使用Maven命令

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=AsyncServiceTest
mvn test -Dtest=MultithreadingControllerTest
mvn test -Dtest=MultithreadingIntegrationTest
mvn test -Dtest=MultithreadingPerformanceTest

# 运行特定测试方法
mvn test -Dtest=AsyncServiceTest#testBasicAsyncTask
```

### 方式三：使用IDE

在IDE中直接运行测试类或测试方法：
- 右键点击测试类 → Run
- 右键点击测试方法 → Run

## 测试详情

### 单元测试详情

#### AsyncServiceTest
测试异步服务的各种功能：

- `testBasicAsyncTask()`: 测试基本异步任务
- `testAsyncTaskWithResult()`: 测试带返回值的异步任务
- `testIoIntensiveTask()`: 测试I/O密集型任务
- `testCpuIntensiveTask()`: 测试CPU密集型任务
- `testSimulateNetworkRequest()`: 测试网络请求模拟
- `testSimulateDatabaseOperation()`: 测试数据库操作模拟
- `testBatchAsyncTasks()`: 测试批量异步任务
- `testConcurrentAsyncTasks()`: 测试多个异步任务并发执行
- `testAsyncTaskExecutionTime()`: 测试异步任务执行时间
- `testAsyncTaskExceptionHandling()`: 测试异步任务异常处理
- `testDifferentThreadPools()`: 测试不同线程池的使用

#### MultithreadingControllerTest
测试控制器的API接口：

- `testBasicAsyncTask()`: 测试基本异步任务API
- `testAsyncTaskWithResult()`: 测试带返回值的异步任务API
- `testIoIntensiveTask()`: 测试I/O密集型任务API
- `testCpuIntensiveTask()`: 测试CPU密集型任务API
- `testConcurrentRequests()`: 测试并发网络请求API
- `testConcurrentDbOperations()`: 测试并发数据库操作API
- `testBatchTasks()`: 测试批量异步任务API
- `testMixedTasks()`: 测试混合任务类型API
- `testGetSystemInfo()`: 测试获取系统信息API
- `testApiParameterValidation()`: 测试API参数验证
- `testApiResponseFormat()`: 测试API响应格式
- `testApiErrorHandling()`: 测试API错误处理
- `testApiPerformance()`: 测试API性能

### 集成测试详情

#### MultithreadingIntegrationTest
测试完整的应用功能：

- `testBasicAsyncTaskIntegration()`: 集成测试基本异步任务
- `testAsyncTaskWithResultIntegration()`: 集成测试带返回值的异步任务
- `testIoIntensiveTaskIntegration()`: 集成测试I/O密集型任务
- `testCpuIntensiveTaskIntegration()`: 集成测试CPU密集型任务
- `testConcurrentRequestsIntegration()`: 集成测试并发网络请求
- `testConcurrentDbOperationsIntegration()`: 集成测试并发数据库操作
- `testBatchTasksIntegration()`: 集成测试批量异步任务
- `testMixedTasksIntegration()`: 集成测试混合任务类型
- `testGetSystemInfoIntegration()`: 集成测试获取系统信息
- `testAsyncServiceDirectCall()`: 集成测试异步服务直接调用
- `testMultipleAsyncTasksConcurrent()`: 集成测试多个异步任务并发执行
- `testApplicationStartupAndHealthCheck()`: 集成测试应用启动和健康检查
- `testPerformance()`: 集成测试性能
- `testErrorHandling()`: 集成测试错误处理

### 性能测试详情

#### MultithreadingPerformanceTest
测试多线程性能表现：

- `testSingleThreadVsMultiThreadPerformance()`: 单线程 vs 多线程执行时间对比
- `testDifferentThreadPoolsPerformance()`: 不同线程池的性能对比
- `testHighConcurrencyPerformance()`: 大量并发任务处理能力
- `testMemoryUsage()`: 内存使用情况
- `testThreadPoolUtilization()`: 线程池利用率
- `testResponseTimeDistribution()`: 响应时间分布
- `testSystemResourceMonitoring()`: 系统资源监控
- `testStressTest()`: 压力测试

## 测试配置

### 测试环境配置
测试使用 `application-test.yml` 配置文件，包含：
- 随机端口配置（避免端口冲突）
- 较小的线程池配置（适合测试）
- 详细的日志配置

### 测试依赖
项目使用以下测试依赖：
- `spring-boot-starter-test`: Spring Boot测试启动器
- `junit-jupiter`: JUnit 5测试框架
- `mockito`: Mock测试框架
- `assertj`: 断言库

## 测试报告

### 生成测试报告

```bash
# 生成Surefire测试报告
mvn surefire-report:report

# 生成测试覆盖率报告
mvn jacoco:report

# 生成完整的测试报告
mvn site
```

### 查看测试报告

- **Surefire报告**: `target/site/surefire-report.html`
- **测试覆盖率**: `target/site/jacoco/index.html`
- **完整报告**: `target/site/index.html`

## 测试最佳实践

### 1. 测试命名
- 测试方法名应该描述测试的场景
- 使用 `@DisplayName` 注解提供更友好的测试名称

### 2. 测试结构
- 使用 `@BeforeEach` 和 `@AfterEach` 进行测试前后的准备和清理
- 使用 Given-When-Then 结构组织测试代码

### 3. 断言
- 使用具体的断言方法而不是通用的 `assertTrue`
- 提供有意义的错误消息

### 4. 异步测试
- 使用 `CompletableFuture.get()` 等待异步任务完成
- 设置合理的超时时间
- 测试异常情况

### 5. 性能测试
- 测试应该在合理的时间内完成
- 监控内存使用情况
- 测试不同负载下的性能表现

## 常见问题

### 1. 测试超时
如果测试超时，可以：
- 增加超时时间
- 检查测试逻辑是否正确
- 确保异步任务能正常完成

### 2. 端口冲突
如果遇到端口冲突：
- 使用随机端口配置
- 确保测试环境配置正确

### 3. 内存不足
如果测试时内存不足：
- 增加JVM内存参数：`-Xmx2g`
- 优化测试数据量
- 及时释放资源

### 4. 测试不稳定
如果测试结果不稳定：
- 检查测试的并发性
- 确保测试环境的一致性
- 使用适当的等待机制

## 持续集成

### GitHub Actions 配置示例

```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 8
      uses: actions/setup-java@v2
      with:
        java-version: '8'
        distribution: 'adopt'
    - name: Run tests
      run: mvn test
    - name: Generate test report
      run: mvn surefire-report:report
```

## 总结

通过运行这些测试，您可以：
1. 验证多线程功能的正确性
2. 测试不同场景下的性能表现
3. 确保代码的稳定性和可靠性
4. 监控系统资源的使用情况

建议在开发过程中定期运行测试，确保代码质量。
