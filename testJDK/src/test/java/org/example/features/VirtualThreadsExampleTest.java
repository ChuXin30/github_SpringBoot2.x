package org.example.features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Virtual Threads 特性单元测试
 */
public class VirtualThreadsExampleTest {

    @Test
    @DisplayName("测试基本虚拟线程创建")
    @Timeout(5)
    void testBasicVirtualThreadCreation() throws InterruptedException {
        // Given
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);
        
        // When
        Thread virtualThread = Thread.ofVirtual()
                .name("test-virtual-thread")
                .start(() -> {
                    counter.incrementAndGet();
                    assertThat(Thread.currentThread().isVirtual()).isTrue();
                    assertThat(Thread.currentThread().getName()).isEqualTo("test-virtual-thread");
                    latch.countDown();
                });
        
        // Then
        latch.await();
        virtualThread.join();
        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("测试虚拟线程 ExecutorService")
    @Timeout(10)
    void testVirtualThreadExecutor() throws InterruptedException, ExecutionException {
        // Given
        int taskCount = 5;
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        // When
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new java.util.ArrayList<>();
            
            for (int i = 0; i < taskCount; i++) {
                final int taskId = i;
                Future<String> future = executor.submit(() -> {
                    Thread.sleep(100); // 模拟工作
                    completedTasks.incrementAndGet();
                    return "任务 " + taskId + " 完成";
                });
                futures.add(future);
            }
            
            // Then
            for (Future<String> future : futures) {
                String result = future.get();
                assertThat(result).contains("任务");
                assertThat(result).contains("完成");
            }
            
            assertThat(completedTasks.get()).isEqualTo(taskCount);
        }
    }

    @Test
    @DisplayName("测试虚拟线程异常处理")
    @Timeout(5)
    void testVirtualThreadExceptionHandling() throws InterruptedException {
        // Given
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        // When
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new java.util.ArrayList<>();
            
            // 添加成功任务
            for (int i = 0; i < 3; i++) {
                final int taskId = i;
                Future<String> future = executor.submit(() -> {
                    successCount.incrementAndGet();
                    return "任务 " + taskId + " 成功";
                });
                futures.add(future);
            }
            
            // 添加失败任务
            Future<String> failureFuture = executor.submit(() -> {
                throw new RuntimeException("故意失败的任务");
            });
            futures.add(failureFuture);
            
            // Then
            for (int i = 0; i < futures.size() - 1; i++) {
                String result = futures.get(i).get();
                assertThat(result).contains("成功");
            }
            
            // 验证异常任务
            assertThatThrownBy(() -> futures.get(futures.size() - 1).get())
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(RuntimeException.class)
                    .hasMessageContaining("故意失败的任务");
            
            assertThat(successCount.get()).isEqualTo(3);
        } catch (ExecutionException e) {
            // 预期的异常
        }
    }

    @Test
    @DisplayName("测试虚拟线程取消操作")
    @Timeout(5)
    void testVirtualThreadCancellation() throws InterruptedException {
        // Given
        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicInteger cancelledCount = new AtomicInteger(0);
        
        // When
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new java.util.ArrayList<>();
            
            // 创建长时间运行的任务
            for (int i = 0; i < 5; i++) {
                final int taskId = i;
                Future<String> future = executor.submit(() -> {
                    try {
                        Thread.sleep(2000); // 长时间运行
                        completedCount.incrementAndGet();
                        return "任务 " + taskId + " 完成";
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        cancelledCount.incrementAndGet();
                        return "任务 " + taskId + " 被取消";
                    }
                });
                futures.add(future);
            }
            
            // 取消一些任务
            futures.get(1).cancel(true);
            futures.get(3).cancel(true);
            
            // 等待其他任务完成
            Thread.sleep(100);
            
            // Then
            for (int i = 0; i < futures.size(); i++) {
                Future<String> future = futures.get(i);
                if (i == 1 || i == 3) {
                    assertThat(future.isCancelled()).isTrue();
                } else {
                    try {
                        String result = future.get(1, TimeUnit.SECONDS);
                        assertThat(result).contains("完成");
                    } catch (TimeoutException | ExecutionException e) {
                        // 任务可能还在运行或被中断，这是正常的
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("测试虚拟线程本地变量")
    @Timeout(5)
    void testVirtualThreadLocalVariables() throws InterruptedException, ExecutionException {
        // Given
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        
        // When
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new java.util.ArrayList<>();
            
            for (int i = 0; i < 3; i++) {
                final int taskId = i;
                Future<String> future = executor.submit(() -> {
                    threadLocal.set("虚拟线程-" + taskId + "-的值");
                    Thread.sleep(100);
                    return Thread.currentThread().getName() + ": " + threadLocal.get();
                });
                futures.add(future);
            }
            
            // Then
            for (Future<String> future : futures) {
                String result = future.get();
                assertThat(result).contains("虚拟线程-");
                assertThat(result).contains("-的值");
            }
        }
    }

    @Test
    @DisplayName("测试虚拟线程阻塞操作")
    @Timeout(10)
    void testVirtualThreadBlockingOperations() throws InterruptedException, ExecutionException {
        // Given
        AtomicInteger completedCount = new AtomicInteger(0);
        
        // When
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new java.util.ArrayList<>();
            
            // 模拟不同的阻塞操作
            for (int i = 0; i < 3; i++) {
                final int taskId = i;
                Future<String> future = executor.submit(() -> {
                    try {
                        // 模拟 I/O 操作
                        Thread.sleep(500);
                        completedCount.incrementAndGet();
                        return "阻塞任务 " + taskId + " 完成";
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return "阻塞任务 " + taskId + " 被中断";
                    }
                });
                futures.add(future);
            }
            
            // Then
            for (Future<String> future : futures) {
                String result = future.get();
                assertThat(result).contains("阻塞任务");
                assertThat(result).contains("完成");
            }
            
            assertThat(completedCount.get()).isEqualTo(3);
        }
    }

    @Test
    @DisplayName("测试虚拟线程信息")
    @Timeout(5)
    void testVirtualThreadInformation() throws InterruptedException, ExecutionException {
        // When
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> future = executor.submit(() -> {
                Thread currentThread = Thread.currentThread();
                return String.format("名称: %s, 虚拟线程: %s, 线程组: %s, 优先级: %d",
                        currentThread.getName(),
                        currentThread.isVirtual(),
                        currentThread.getThreadGroup().getName(),
                        currentThread.getPriority());
            });
            
            // Then
            String result = future.get();
            assertThat(result).contains("虚拟线程: true");
            assertThat(result).contains("优先级: 5"); // 默认优先级
        }
    }

    @Test
    @DisplayName("测试虚拟线程性能 - 大量任务")
    @Timeout(30)
    void testVirtualThreadPerformance() throws InterruptedException {
        // Given
        int taskCount = 1000;
        AtomicInteger completedTasks = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
        
        // When
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Void>> futures = new java.util.ArrayList<>();
            
            for (int i = 0; i < taskCount; i++) {
                Future<Void> future = executor.submit(() -> {
                    try {
                        Thread.sleep(10); // 模拟 I/O 操作
                        completedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return null;
                });
                futures.add(future);
            }
            
            // 等待所有任务完成
            for (Future<Void> future : futures) {
                try {
                    future.get(5, TimeUnit.SECONDS);
                } catch (TimeoutException | ExecutionException e) {
                    // 忽略超时或执行异常
                }
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Then
        assertThat(completedTasks.get()).isGreaterThan(0);
        assertThat(duration).isLessThan(10000); // 应该在10秒内完成大部分任务
        System.out.println("完成 " + completedTasks.get() + " 个任务，耗时: " + duration + "ms");
    }

    @Test
    @DisplayName("测试虚拟线程与 CompletableFuture 结合使用")
    @Timeout(5)
    void testVirtualThreadWithCompletableFuture() throws InterruptedException, ExecutionException {
        // Given
        AtomicInteger completedCount = new AtomicInteger(0);
        
        // When
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                completedCount.incrementAndGet();
                return "任务1完成";
            }, executor);
            
            CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                completedCount.incrementAndGet();
                return "任务2完成";
            }, executor);
            
            CompletableFuture<String> combinedFuture = future1.thenCombine(future2, (result1, result2) -> {
                return result1 + " + " + result2;
            });
            
            // Then
            String result = combinedFuture.get();
            assertThat(result).isEqualTo("任务1完成 + 任务2完成");
            assertThat(completedCount.get()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("测试虚拟线程的线程池关闭")
    @Timeout(5)
    void testVirtualThreadExecutorShutdown() throws InterruptedException {
        // Given
        AtomicInteger runningTasks = new AtomicInteger(0);
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        // When
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        
        // 提交一些任务
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                runningTasks.incrementAndGet();
                try {
                    Thread.sleep(1000);
                    completedTasks.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                runningTasks.decrementAndGet();
                return "任务 " + taskId + " 完成";
            });
        }
        
        // 等待任务开始
        Thread.sleep(100);
        
        // 关闭执行器
        executor.shutdown();
        boolean terminated = executor.awaitTermination(3, TimeUnit.SECONDS);
        
        // Then
        assertThat(terminated).isTrue();
        assertThat(executor.isShutdown()).isTrue();
        assertThat(completedTasks.get()).isEqualTo(5);
    }

    @Test
    @DisplayName("测试虚拟线程的并发安全性")
    @Timeout(10)
    void testVirtualThreadConcurrencySafety() throws InterruptedException, ExecutionException {
        // Given
        AtomicInteger counter = new AtomicInteger(0);
        int threadCount = 100;
        int incrementsPerThread = 100;
        
        // When
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Void>> futures = new java.util.ArrayList<>();
            
            for (int i = 0; i < threadCount; i++) {
                Future<Void> future = executor.submit(() -> {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counter.incrementAndGet();
                        // 添加一些随机延迟来增加竞争条件
                        if (j % 10 == 0) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                    return null;
                });
                futures.add(future);
            }
            
            // 等待所有任务完成
            for (Future<Void> future : futures) {
                future.get();
            }
        }
        
        // Then
        int expectedValue = threadCount * incrementsPerThread;
        assertThat(counter.get()).isEqualTo(expectedValue);
    }
}
