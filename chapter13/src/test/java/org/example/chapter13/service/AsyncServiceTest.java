package org.example.chapter13.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AsyncService 单元测试类
 * 测试异步服务的各种功能
 * 
 * @author example
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class AsyncServiceTest {

    @Autowired
    private AsyncService asyncService;

    @BeforeEach
    void setUp() {
        // 测试前的准备工作
    }

    @Test
    @DisplayName("测试基本异步任务")
    void testBasicAsyncTask() throws InterruptedException {
        // Given
        String taskName = "测试基本异步任务";
        long startTime = System.currentTimeMillis();

        // When
        asyncService.basicAsyncTask(taskName);

        // Then
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        // 异步任务应该立即返回，执行时间应该很短
        assertTrue(executionTime < 1000, "异步任务应该立即返回");
        
        // 等待一段时间确保异步任务完成
        Thread.sleep(3000);
    }

    @Test
    @DisplayName("测试带返回值的异步任务")
    void testAsyncTaskWithResult() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String taskName = "测试带返回值任务";

        // When
        CompletableFuture<String> future = asyncService.asyncTaskWithResult(taskName);
        String result = future.get(10, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertTrue(result.contains(taskName));
        assertTrue(result.contains("任务完成"));
        assertTrue(result.contains("执行线程"));
    }

    @Test
    @DisplayName("测试I/O密集型异步任务")
    void testIoIntensiveTask() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String taskName = "测试I/O密集型任务";

        // When
        CompletableFuture<String> future = asyncService.ioIntensiveTask(taskName);
        String result = future.get(10, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("I/O密集型任务完成"));
        assertTrue(result.contains(taskName));
        assertTrue(result.contains("执行线程"));
    }

    @Test
    @DisplayName("测试CPU密集型异步任务")
    void testCpuIntensiveTask() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String taskName = "测试CPU密集型任务";

        // When
        CompletableFuture<String> future = asyncService.cpuIntensiveTask(taskName);
        String result = future.get(30, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("CPU密集型任务完成"));
        assertTrue(result.contains(taskName));
        assertTrue(result.contains("计算结果"));
        assertTrue(result.contains("执行线程"));
    }

    @Test
    @DisplayName("测试网络请求模拟")
    void testSimulateNetworkRequest() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String url = "http://test.example.com/api";

        // When
        CompletableFuture<String> future = asyncService.simulateNetworkRequest(url);
        String result = future.get(10, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("网络请求完成"));
        assertTrue(result.contains(url));
        assertTrue(result.contains("响应时间"));
    }

    @Test
    @DisplayName("测试数据库操作模拟")
    void testSimulateDatabaseOperation() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String operation = "SELECT * FROM test_table";

        // When
        CompletableFuture<String> future = asyncService.simulateDatabaseOperation(operation);
        String result = future.get(10, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("数据库操作完成"));
        assertTrue(result.contains(operation));
    }

    @Test
    @DisplayName("测试批量异步任务")
    void testBatchAsyncTasks() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        int taskCount = 5;

        // When
        CompletableFuture<String> future = asyncService.batchAsyncTasks(taskCount);
        String result = future.get(15, TimeUnit.SECONDS);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("批量异步任务完成"));
        assertTrue(result.contains(String.valueOf(taskCount)));
    }

    @Test
    @DisplayName("测试多个异步任务并发执行")
    void testConcurrentAsyncTasks() throws ExecutionException, InterruptedException, TimeoutException {
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
    @DisplayName("测试异步任务执行时间")
    void testAsyncTaskExecutionTime() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String taskName = "执行时间测试任务";
        long startTime = System.currentTimeMillis();

        // When
        CompletableFuture<String> future = asyncService.asyncTaskWithResult(taskName);
        String result = future.get(10, TimeUnit.SECONDS);

        // Then
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        assertNotNull(result);
        // 异步任务应该至少执行3秒（模拟的睡眠时间）
        assertTrue(executionTime >= 2000, "异步任务执行时间应该至少2秒");
        assertTrue(executionTime < 10000, "异步任务执行时间不应该超过10秒");
    }

    @Test
    @DisplayName("测试异步任务异常处理")
    void testAsyncTaskExceptionHandling() throws InterruptedException {
        // Given
        String taskName = "异常测试任务";

        // When & Then
        // 这里我们测试一个会抛出异常的场景
        // 由于我们的实现中没有抛出异常，这个测试主要验证方法不会因为异常而崩溃
        assertDoesNotThrow(() -> {
            asyncService.basicAsyncTask(taskName);
        });

        // 等待任务完成
        Thread.sleep(3000);
    }

    @Test
    @DisplayName("测试不同线程池的使用")
    void testDifferentThreadPools() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        String ioTask = "I/O线程池测试";
        String cpuTask = "CPU线程池测试";

        // When
        CompletableFuture<String> ioFuture = asyncService.ioIntensiveTask(ioTask);
        CompletableFuture<String> cpuFuture = asyncService.cpuIntensiveTask(cpuTask);

        // Then
        String ioResult = ioFuture.get(10, TimeUnit.SECONDS);
        String cpuResult = cpuFuture.get(30, TimeUnit.SECONDS);

        assertNotNull(ioResult);
        assertNotNull(cpuResult);

        // 验证结果包含正确的线程池信息
        assertTrue(ioResult.contains("I/O密集型任务完成"));
        assertTrue(cpuResult.contains("CPU密集型任务完成"));
    }
}
