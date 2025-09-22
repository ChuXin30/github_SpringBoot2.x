package org.example.chapter13.integration;

import org.example.chapter13.Chapter13Application;
import org.example.chapter13.service.AsyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 多线程功能集成测试类
 * 测试完整的应用功能，包括HTTP接口和异步服务
 * 
 * @author example
 * @since 1.0.0
 */
@SpringBootTest(classes = Chapter13Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MultithreadingIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AsyncService asyncService;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/chapter13/api/multithreading";
    }

    @Test
    @DisplayName("集成测试 - 基本异步任务")
    void testBasicAsyncTaskIntegration() throws InterruptedException {
        // Given
        String taskName = "集成测试任务";

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/basic-async?taskName=" + taskName, 
                null, 
                Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("基本异步任务已提交", response.getBody().get("message"));
        assertEquals(taskName, response.getBody().get("taskName"));

        // 等待异步任务完成
        Thread.sleep(3000);
    }

    @Test
    @DisplayName("集成测试 - 带返回值的异步任务")
    void testAsyncTaskWithResultIntegration() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String taskName = "集成测试带返回值任务";

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/async-with-result?taskName=" + taskName, 
                null, 
                Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("result").toString().contains(taskName));
        assertTrue(response.getBody().get("result").toString().contains("任务完成"));
    }

    @Test
    @DisplayName("集成测试 - I/O密集型任务")
    void testIoIntensiveTaskIntegration() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String taskName = "集成测试I/O任务";

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/io-intensive?taskName=" + taskName, 
                null, 
                Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("result").toString().contains("I/O密集型任务完成"));
        assertEquals("I/O密集型", response.getBody().get("taskType"));
    }

    @Test
    @DisplayName("集成测试 - CPU密集型任务")
    void testCpuIntensiveTaskIntegration() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String taskName = "集成测试CPU任务";

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/cpu-intensive?taskName=" + taskName, 
                null, 
                Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("result").toString().contains("CPU密集型任务完成"));
        assertEquals("CPU密集型", response.getBody().get("taskType"));
    }

    @Test
    @DisplayName("集成测试 - 并发网络请求")
    void testConcurrentRequestsIntegration() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        int requestCount = 3;

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/concurrent-requests?requestCount=" + requestCount, 
                null, 
                Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(requestCount, response.getBody().get("requestCount"));
        assertTrue(response.getBody().get("results") instanceof java.util.List);
        
        @SuppressWarnings("unchecked")
        java.util.List<String> results = (java.util.List<String>) response.getBody().get("results");
        assertEquals(requestCount, results.size());
    }

    @Test
    @DisplayName("集成测试 - 并发数据库操作")
    void testConcurrentDbOperationsIntegration() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        int operationCount = 2;

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/concurrent-db-operations?operationCount=" + operationCount, 
                null, 
                Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(operationCount, response.getBody().get("operationCount"));
        assertTrue(response.getBody().get("results") instanceof java.util.List);
    }

    @Test
    @DisplayName("集成测试 - 批量异步任务")
    void testBatchTasksIntegration() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        int taskCount = 5;

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/batch-tasks?taskCount=" + taskCount, 
                null, 
                Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskCount, response.getBody().get("taskCount"));
        assertTrue(response.getBody().get("result").toString().contains("批量异步任务完成"));
    }

    @Test
    @DisplayName("集成测试 - 混合任务类型")
    void testMixedTasksIntegration() throws ExecutionException, InterruptedException, TimeoutException {
        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/mixed-tasks", 
                null, 
                Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("ioTaskResult"));
        assertTrue(response.getBody().containsKey("cpuTaskResult"));
        assertTrue(response.getBody().containsKey("networkTaskResult"));
        assertTrue(response.getBody().containsKey("totalExecutionTime"));
    }

    @Test
    @DisplayName("集成测试 - 获取系统信息")
    void testGetSystemInfoIntegration() {
        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/system-info", 
                Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("availableProcessors"));
        assertTrue(response.getBody().containsKey("maxMemory"));
        assertTrue(response.getBody().containsKey("totalMemory"));
        assertTrue(response.getBody().containsKey("freeMemory"));
        assertTrue(response.getBody().containsKey("currentThread"));
    }

    @Test
    @DisplayName("集成测试 - 异步服务直接调用")
    void testAsyncServiceDirectCall() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String taskName = "直接调用测试";

        // When
        CompletableFuture<String> future = asyncService.asyncTaskWithResult(taskName);
        String result = future.get(10, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertTrue(result.contains(taskName));
        assertTrue(result.contains("任务完成"));
    }

    @Test
    @DisplayName("集成测试 - 多个异步任务并发执行")
    void testMultipleAsyncTasksConcurrent() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String task1 = "并发任务1";
        String task2 = "并发任务2";
        String task3 = "并发任务3";

        // When
        CompletableFuture<String> future1 = asyncService.asyncTaskWithResult(task1);
        CompletableFuture<String> future2 = asyncService.ioIntensiveTask(task2);
        CompletableFuture<String> future3 = asyncService.simulateNetworkRequest("http://test.com");

        // Then
        String result1 = future1.get(10, TimeUnit.SECONDS);
        String result2 = future2.get(10, TimeUnit.SECONDS);
        String result3 = future3.get(10, TimeUnit.SECONDS);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);

        assertTrue(result1.contains(task1));
        assertTrue(result2.contains(task2));
        assertTrue(result3.contains("网络请求完成"));
    }

    @Test
    @DisplayName("集成测试 - 应用启动和健康检查")
    void testApplicationStartupAndHealthCheck() {
        // 测试应用是否能正常启动
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/chapter13/actuator/health", 
                Map.class
        );

        // 应用应该能正常响应健康检查
        assertTrue(response.getStatusCode() == HttpStatus.OK || 
                  response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    @DisplayName("集成测试 - 性能测试")
    void testPerformance() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        int taskCount = 10;
        long startTime = System.currentTimeMillis();

        // When
        CompletableFuture<String>[] futures = new CompletableFuture[taskCount];
        for (int i = 0; i < taskCount; i++) {
            futures[i] = asyncService.asyncTaskWithResult("性能测试任务" + i);
        }

        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures);
        allFutures.get(30, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Then
        assertTrue(totalTime < 30000, "10个并发任务应该在30秒内完成");
        
        // 验证所有任务都完成了
        for (CompletableFuture<String> future : futures) {
            assertTrue(future.isDone());
            assertNotNull(future.get());
        }
    }

    @Test
    @DisplayName("集成测试 - 错误处理")
    void testErrorHandling() {
        // 测试无效的请求参数
        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/concurrent-requests?requestCount=0", 
                null, 
                Map.class
        );

        // 应该能处理无效参数，不会崩溃
        assertTrue(response.getStatusCode() == HttpStatus.OK || 
                  response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }
}
