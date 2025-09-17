package org.example.features;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Java 21 Virtual Threads 特性示例
 * Virtual Threads 是 Java 21 中引入的轻量级线程，可以显著提高并发性能
 */
public class VirtualThreadsExample {

    // 基本 Virtual Thread 创建示例
    public static void createBasicVirtualThread() {
        System.out.println("=== 基本 Virtual Thread 创建 ===");
        
        // 使用 Thread.ofVirtual() 创建虚拟线程
        Thread virtualThread = Thread.ofVirtual()
                .name("virtual-thread-1")
                .start(() -> {
                    System.out.println("虚拟线程执行中: " + Thread.currentThread().getName());
                    System.out.println("是否为虚拟线程: " + Thread.currentThread().isVirtual());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("虚拟线程执行完成");
                });
        
        try {
            virtualThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 使用 ExecutorService 创建虚拟线程
    public static void createVirtualThreadExecutor() throws InterruptedException {
        System.out.println("\n=== 使用 ExecutorService 创建虚拟线程 ===");
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = IntStream.range(0, 5)
                    .mapToObj(i -> executor.submit(() -> {
                        String threadName = Thread.currentThread().getName();
                        System.out.println("任务 " + i + " 在虚拟线程 " + threadName + " 中执行");
                        Thread.sleep(1000);
                        return "任务 " + i + " 完成";
                    }))
                    .toList();
            
            for (Future<String> future : futures) {
                System.out.println(future.get());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // 比较传统线程和虚拟线程的性能
    public static void compareThreadPerformance() throws InterruptedException {
        System.out.println("\n=== 传统线程 vs 虚拟线程性能比较 ===");
        
        int taskCount = 10000;
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        // 测试传统线程
        System.out.println("测试传统线程...");
        Instant start = Instant.now();
        
        try (ExecutorService traditionalExecutor = Executors.newFixedThreadPool(100)) {
            for (int i = 0; i < taskCount; i++) {
                traditionalExecutor.submit(() -> {
                    try {
                        Thread.sleep(10); // 模拟 I/O 操作
                        completedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        
        Instant traditionalEnd = Instant.now();
        Duration traditionalDuration = Duration.between(start, traditionalEnd);
        System.out.println("传统线程完成时间: " + traditionalDuration.toMillis() + "ms");
        System.out.println("完成的任务数: " + completedTasks.get());
        
        // 重置计数器
        completedTasks.set(0);
        
        // 测试虚拟线程
        System.out.println("\n测试虚拟线程...");
        start = Instant.now();
        
        try (ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                virtualExecutor.submit(() -> {
                    try {
                        Thread.sleep(10); // 模拟 I/O 操作
                        completedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        
        Instant virtualEnd = Instant.now();
        Duration virtualDuration = Duration.between(start, virtualEnd);
        System.out.println("虚拟线程完成时间: " + virtualDuration.toMillis() + "ms");
        System.out.println("完成的任务数: " + completedTasks.get());
        
        System.out.println("\n性能提升: " + 
                (traditionalDuration.toMillis() - virtualDuration.toMillis()) + "ms");
    }

    // 虚拟线程的异常处理
    public static void handleVirtualThreadExceptions() throws InterruptedException {
        System.out.println("\n=== 虚拟线程异常处理 ===");
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = IntStream.range(0, 5)
                    .mapToObj(i -> executor.submit(() -> {
                        if (i == 2) {
                            throw new RuntimeException("任务 " + i + " 故意抛出异常");
                        }
                        return "任务 " + i + " 成功完成";
                    }))
                    .toList();
            
            for (int i = 0; i < futures.size(); i++) {
                try {
                    String result = futures.get(i).get();
                    System.out.println(result);
                } catch (ExecutionException e) {
                    System.out.println("任务 " + i + " 执行失败: " + e.getCause().getMessage());
                }
            }
        }
    }

    // 虚拟线程的取消操作
    public static void cancelVirtualThreads() throws InterruptedException {
        System.out.println("\n=== 虚拟线程取消操作 ===");
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = IntStream.range(0, 5)
                    .mapToObj(i -> executor.submit(() -> {
                        try {
                            Thread.sleep(2000); // 长时间运行的任务
                            return "任务 " + i + " 完成";
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return "任务 " + i + " 被中断";
                        }
                    }))
                    .toList();
            
            // 取消一些任务
            futures.get(1).cancel(true);
            futures.get(3).cancel(true);
            
            for (int i = 0; i < futures.size(); i++) {
                try {
                    if (!futures.get(i).isCancelled()) {
                        String result = futures.get(i).get(1, TimeUnit.SECONDS);
                        System.out.println(result);
                    } else {
                        System.out.println("任务 " + i + " 已被取消");
                    }
                } catch (ExecutionException | TimeoutException e) {
                    System.out.println("任务 " + i + " 执行超时或被中断");
                }
            }
        }
    }

    // 虚拟线程的线程本地变量
    public static void virtualThreadLocalVariables() throws InterruptedException {
        System.out.println("\n=== 虚拟线程本地变量 ===");
        
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = IntStream.range(0, 5)
                    .mapToObj(i -> executor.submit(() -> {
                        threadLocal.set("虚拟线程-" + i + "-的值");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        return Thread.currentThread().getName() + ": " + threadLocal.get();
                    }))
                    .toList();
            
            for (Future<String> future : futures) {
                System.out.println(future.get());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // 虚拟线程的阻塞操作
    public static void virtualThreadBlockingOperations() throws InterruptedException {
        System.out.println("\n=== 虚拟线程阻塞操作 ===");
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = IntStream.range(0, 3)
                    .mapToObj(i -> executor.submit(() -> {
                        System.out.println("任务 " + i + " 开始执行");
                        
                        // 模拟不同的阻塞操作
                        switch (i) {
                            case 0 -> {
                                // 文件 I/O 操作
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                            case 1 -> {
                                // 网络 I/O 操作
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                            case 2 -> {
                                // 数据库操作
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                        
                        System.out.println("任务 " + i + " 执行完成");
                        return "任务 " + i + " 结果";
                    }))
                    .toList();
            
            for (Future<String> future : futures) {
                System.out.println(future.get());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // 虚拟线程的监控和调试
    public static void monitorVirtualThreads() throws InterruptedException {
        System.out.println("\n=== 虚拟线程监控 ===");
        
        // 启用虚拟线程监控
        System.setProperty("jdk.traceVirtualThreads", "true");
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = IntStream.range(0, 3)
                    .mapToObj(i -> executor.submit(() -> {
                        System.out.println("虚拟线程信息:");
                        System.out.println("  名称: " + Thread.currentThread().getName());
                        System.out.println("  是否为虚拟线程: " + Thread.currentThread().isVirtual());
                        System.out.println("  线程组: " + Thread.currentThread().getThreadGroup().getName());
                        System.out.println("  优先级: " + Thread.currentThread().getPriority());
                        
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        return "监控任务 " + i + " 完成";
                    }))
                    .toList();
            
            for (Future<String> future : futures) {
                System.out.println(future.get());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // 演示方法
    public static void demonstrateVirtualThreads() {
        try {
            System.out.println("=== Virtual Threads 特性演示 ===");
            
            createBasicVirtualThread();
            createVirtualThreadExecutor();
            compareThreadPerformance();
            handleVirtualThreadExceptions();
            cancelVirtualThreads();
            virtualThreadLocalVariables();
            virtualThreadBlockingOperations();
            monitorVirtualThreads();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("演示被中断: " + e.getMessage());
        }
    }

    // 虚拟线程最佳实践示例
    public static void virtualThreadBestPractices() throws InterruptedException {
        System.out.println("\n=== 虚拟线程最佳实践 ===");
        
        // 1. 使用 try-with-resources 确保 ExecutorService 正确关闭
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            // 2. 避免在虚拟线程中使用 ThreadLocal 进行缓存
            // 3. 虚拟线程适合 I/O 密集型任务
            List<Future<String>> futures = IntStream.range(0, 10)
                    .mapToObj(i -> executor.submit(() -> {
                        // 模拟 I/O 操作
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return "任务 " + i + " 被中断";
                        }
                        return "任务 " + i + " 完成 (虚拟线程: " + 
                                Thread.currentThread().isVirtual() + ")";
                    }))
                    .toList();
            
            // 4. 使用 CompletableFuture 进行异步编程
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "CompletableFuture 任务完成";
            }, executor);
            
            // 等待所有任务完成
            for (Future<String> f : futures) {
                System.out.println(f.get());
            }
            
            System.out.println(future.get());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
