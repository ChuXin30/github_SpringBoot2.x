package org.example.chapter13.performance;

import org.example.chapter13.Chapter13Application;
import org.example.chapter13.service.AsyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 多线程性能测试类
 * 测试不同场景下的多线程性能表现
 * 
 * @author example
 * @since 1.0.0
 */
@SpringBootTest(classes = Chapter13Application.class)
@ActiveProfiles("test")
class MultithreadingPerformanceTest {

    @Autowired
    private AsyncService asyncService;

    private long startTime;
    private long endTime;

    @BeforeEach
    void setUp() {
        startTime = System.currentTimeMillis();
    }

    @AfterEach
    void tearDown() {
        endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("测试执行时间: " + executionTime + "ms");
    }

    @Test
    @DisplayName("性能测试 - 单线程 vs 多线程执行时间对比")
    void testSingleThreadVsMultiThreadPerformance() throws ExecutionException, InterruptedException, TimeoutException {
        int taskCount = 10;
        
        // 单线程执行
        long singleThreadStart = System.currentTimeMillis();
        for (int i = 0; i < taskCount; i++) {
            CompletableFuture<String> future = asyncService.asyncTaskWithResult("单线程任务" + i);
            future.get(10, TimeUnit.SECONDS);
        }
        long singleThreadEnd = System.currentTimeMillis();
        long singleThreadTime = singleThreadEnd - singleThreadStart;
        
        // 多线程并发执行
        long multiThreadStart = System.currentTimeMillis();
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            futures.add(asyncService.asyncTaskWithResult("多线程任务" + i));
        }
        
        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.get(30, TimeUnit.SECONDS);
        long multiThreadEnd = System.currentTimeMillis();
        long multiThreadTime = multiThreadEnd - multiThreadStart;
        
        System.out.println("单线程执行时间: " + singleThreadTime + "ms");
        System.out.println("多线程执行时间: " + multiThreadTime + "ms");
        System.out.println("性能提升: " + ((double)(singleThreadTime - multiThreadTime) / singleThreadTime * 100) + "%");
        
        // 多线程应该比单线程快
        assertTrue(multiThreadTime < singleThreadTime, "多线程执行应该比单线程快");
    }

    @Test
    @DisplayName("性能测试 - 不同线程池的性能对比")
    void testDifferentThreadPoolsPerformance() throws ExecutionException, InterruptedException, TimeoutException {
        int taskCount = 5;
        
        // 测试I/O密集型线程池
        long ioStart = System.currentTimeMillis();
        List<CompletableFuture<String>> ioFutures = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            ioFutures.add(asyncService.ioIntensiveTask("I/O任务" + i));
        }
        CompletableFuture<Void> ioAllFutures = CompletableFuture.allOf(ioFutures.toArray(new CompletableFuture[0]));
        ioAllFutures.get(30, TimeUnit.SECONDS);
        long ioEnd = System.currentTimeMillis();
        long ioTime = ioEnd - ioStart;
        
        // 测试CPU密集型线程池
        long cpuStart = System.currentTimeMillis();
        List<CompletableFuture<String>> cpuFutures = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            cpuFutures.add(asyncService.cpuIntensiveTask("CPU任务" + i));
        }
        CompletableFuture<Void> cpuAllFutures = CompletableFuture.allOf(cpuFutures.toArray(new CompletableFuture[0]));
        cpuAllFutures.get(60, TimeUnit.SECONDS);
        long cpuEnd = System.currentTimeMillis();
        long cpuTime = cpuEnd - cpuStart;
        
        System.out.println("I/O密集型任务执行时间: " + ioTime + "ms");
        System.out.println("CPU密集型任务执行时间: " + cpuTime + "ms");
        
        // 验证任务都完成了
        assertTrue(ioTime > 0);
        assertTrue(cpuTime > 0);
    }

    @Test
    @DisplayName("性能测试 - 大量并发任务处理能力")
    void testHighConcurrencyPerformance() throws ExecutionException, InterruptedException, TimeoutException {
        int taskCount = 50;
        AtomicInteger completedTasks = new AtomicInteger(0);
        AtomicLong totalExecutionTime = new AtomicLong(0);
        
        long startTime = System.currentTimeMillis();
        
        // 创建大量并发任务
        List<CompletableFuture<String>> futures = IntStream.range(0, taskCount)
                .mapToObj(i -> asyncService.asyncTaskWithResult("高并发任务" + i)
                        .thenApply(result -> {
                            completedTasks.incrementAndGet();
                            return result;
                        }))
                .collect(java.util.stream.Collectors.toList());
        
        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.get(60, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("总任务数: " + taskCount);
        System.out.println("完成任务数: " + completedTasks.get());
        System.out.println("总执行时间: " + totalTime + "ms");
        System.out.println("平均每个任务时间: " + (totalTime / taskCount) + "ms");
        System.out.println("吞吐量: " + (taskCount * 1000.0 / totalTime) + " 任务/秒");
        
        // 验证所有任务都完成了
        assertEquals(taskCount, completedTasks.get());
        assertTrue(totalTime < 60000, "50个并发任务应该在60秒内完成");
    }

    @Test
    @DisplayName("性能测试 - 内存使用情况")
    void testMemoryUsage() throws ExecutionException, InterruptedException, TimeoutException {
        Runtime runtime = Runtime.getRuntime();
        
        // 记录初始内存
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("初始内存使用: " + (initialMemory / 1024 / 1024) + " MB");
        
        // 执行大量任务
        int taskCount = 100;
        List<CompletableFuture<String>> futures = new ArrayList<>();
        
        for (int i = 0; i < taskCount; i++) {
            futures.add(asyncService.asyncTaskWithResult("内存测试任务" + i));
        }
        
        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.get(120, TimeUnit.SECONDS);
        
        // 记录任务完成后的内存
        long afterTaskMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("任务完成后内存使用: " + (afterTaskMemory / 1024 / 1024) + " MB");
        
        // 强制垃圾回收
        System.gc();
        Thread.sleep(1000);
        
        // 记录GC后的内存
        long afterGcMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("GC后内存使用: " + (afterGcMemory / 1024 / 1024) + " MB");
        
        long memoryIncrease = afterGcMemory - initialMemory;
        System.out.println("内存增长: " + (memoryIncrease / 1024 / 1024) + " MB");
        
        // 验证内存使用合理
        assertTrue(memoryIncrease < 100 * 1024 * 1024, "内存增长应该小于100MB");
    }

    @Test
    @DisplayName("性能测试 - 线程池利用率")
    void testThreadPoolUtilization() throws ExecutionException, InterruptedException, TimeoutException {
        int taskCount = 20;
        List<CompletableFuture<String>> futures = new ArrayList<>();
        
        // 创建不同类型的任务来测试不同线程池
        for (int i = 0; i < taskCount / 2; i++) {
            futures.add(asyncService.ioIntensiveTask("I/O任务" + i));
        }
        
        for (int i = 0; i < taskCount / 2; i++) {
            futures.add(asyncService.cpuIntensiveTask("CPU任务" + i));
        }
        
        long startTime = System.currentTimeMillis();
        
        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.get(120, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("混合任务执行时间: " + totalTime + "ms");
        System.out.println("任务总数: " + taskCount);
        System.out.println("平均任务时间: " + (totalTime / taskCount) + "ms");
        
        // 验证任务都完成了
        assertTrue(totalTime > 0);
        assertTrue(totalTime < 120000, "混合任务应该在120秒内完成");
    }

    @Test
    @DisplayName("性能测试 - 响应时间分布")
    void testResponseTimeDistribution() throws ExecutionException, InterruptedException, TimeoutException {
        int taskCount = 30;
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < taskCount; i++) {
            long taskStart = System.currentTimeMillis();
            
            CompletableFuture<String> future = asyncService.asyncTaskWithResult("响应时间测试任务" + i);
            future.get(10, TimeUnit.SECONDS);
            
            long taskEnd = System.currentTimeMillis();
            long responseTime = taskEnd - taskStart;
            responseTimes.add(responseTime);
        }
        
        // 计算统计信息
        long minTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long maxTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        double avgTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        
        System.out.println("响应时间统计:");
        System.out.println("最小响应时间: " + minTime + "ms");
        System.out.println("最大响应时间: " + maxTime + "ms");
        System.out.println("平均响应时间: " + String.format("%.2f", avgTime) + "ms");
        
        // 验证响应时间合理
        assertTrue(minTime > 0, "最小响应时间应该大于0");
        assertTrue(maxTime < 15000, "最大响应时间应该小于15秒");
        assertTrue(avgTime > 0, "平均响应时间应该大于0");
    }

    @Test
    @DisplayName("性能测试 - 系统资源监控")
    void testSystemResourceMonitoring() throws ExecutionException, InterruptedException, TimeoutException {
        Runtime runtime = Runtime.getRuntime();
        
        System.out.println("系统资源信息:");
        System.out.println("可用处理器数: " + runtime.availableProcessors());
        System.out.println("最大内存: " + (runtime.maxMemory() / 1024 / 1024) + " MB");
        System.out.println("总内存: " + (runtime.totalMemory() / 1024 / 1024) + " MB");
        System.out.println("空闲内存: " + (runtime.freeMemory() / 1024 / 1024) + " MB");
        
        // 执行一些任务来测试系统负载
        int taskCount = 10;
        List<CompletableFuture<String>> futures = new ArrayList<>();
        
        for (int i = 0; i < taskCount; i++) {
            futures.add(asyncService.asyncTaskWithResult("系统监控任务" + i));
        }
        
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.get(60, TimeUnit.SECONDS);
        
        System.out.println("任务执行后内存使用: " + ((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024) + " MB");
        
        // 验证系统资源使用合理
        assertTrue(runtime.availableProcessors() > 0, "可用处理器数应该大于0");
        assertTrue(runtime.maxMemory() > 0, "最大内存应该大于0");
    }

    @Test
    @DisplayName("性能测试 - 压力测试")
    void testStressTest() throws ExecutionException, InterruptedException, TimeoutException {
        int taskCount = 100;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // 创建大量任务进行压力测试
        List<CompletableFuture<String>> futures = IntStream.range(0, taskCount)
                .mapToObj(i -> asyncService.asyncTaskWithResult("压力测试任务" + i)
                        .handle((result, throwable) -> {
                            if (throwable != null) {
                                failureCount.incrementAndGet();
                                return "失败: " + throwable.getMessage();
                            } else {
                                successCount.incrementAndGet();
                                return result;
                            }
                        }))
                .collect(java.util.stream.Collectors.toList());
        
        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.get(180, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("压力测试结果:");
        System.out.println("总任务数: " + taskCount);
        System.out.println("成功任务数: " + successCount.get());
        System.out.println("失败任务数: " + failureCount.get());
        System.out.println("成功率: " + (successCount.get() * 100.0 / taskCount) + "%");
        System.out.println("总执行时间: " + totalTime + "ms");
        System.out.println("吞吐量: " + (taskCount * 1000.0 / totalTime) + " 任务/秒");
        
        // 验证压力测试结果
        assertTrue(successCount.get() > taskCount * 0.9, "成功率应该大于90%");
        assertTrue(totalTime < 180000, "压力测试应该在180秒内完成");
    }
}
