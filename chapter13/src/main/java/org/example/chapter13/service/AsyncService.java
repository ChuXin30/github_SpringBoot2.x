package org.example.chapter13.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 异步服务类
 * 演示Spring Boot中@Async注解的使用
 * 
 * @author example
 * @since 1.0.0
 */
@Service
public class AsyncService {

    private static final Logger log = LoggerFactory.getLogger(AsyncService.class);

    /**
     * 基本异步方法
     * 使用默认的线程池执行
     */
    @Async
    public void basicAsyncTask(String taskName) {
        log.info("开始执行异步任务: {}, 线程: {}", taskName, Thread.currentThread().getName());
        
        try {
            // 模拟耗时操作
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("任务被中断: {}", taskName);
        }
        
        log.info("异步任务完成: {}, 线程: {}", taskName, Thread.currentThread().getName());
    }

    /**
     * 带返回值的异步方法
     * 返回CompletableFuture
     */
    @Async
    public CompletableFuture<String> asyncTaskWithResult(String taskName) {
        log.info("开始执行带返回值的异步任务: {}, 线程: {}", taskName, Thread.currentThread().getName());
        
        try {
            // 模拟耗时操作
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("任务被中断: {}", taskName);
            return CompletableFuture.completedFuture("任务被中断: " + taskName);
        }
        
        String result = "任务完成: " + taskName + ", 执行线程: " + Thread.currentThread().getName();
        log.info("异步任务完成: {}", result);
        
        return CompletableFuture.completedFuture(result);
    }

    /**
     * 使用指定线程池的异步方法
     * 使用I/O密集型线程池
     */
    @Async("ioTaskExecutor")
    public CompletableFuture<String> ioIntensiveTask(String taskName) {
        log.info("开始执行I/O密集型任务: {}, 线程: {}", taskName, Thread.currentThread().getName());
        
        try {
            // 模拟I/O操作（如数据库查询、文件读写、网络请求等）
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("I/O任务被中断: {}", taskName);
            return CompletableFuture.completedFuture("I/O任务被中断: " + taskName);
        }
        
        String result = "I/O密集型任务完成: " + taskName + ", 执行线程: " + Thread.currentThread().getName();
        log.info("I/O密集型任务完成: {}", result);
        
        return CompletableFuture.completedFuture(result);
    }

    /**
     * 使用CPU密集型线程池的异步方法
     */
    @Async("cpuTaskExecutor")
    public CompletableFuture<String> cpuIntensiveTask(String taskName) {
        log.info("开始执行CPU密集型任务: {}, 线程: {}", taskName, Thread.currentThread().getName());
        
        // 模拟CPU密集型计算
        long result = 0;
        for (int i = 0; i < 100000000; i++) {
            result += i;
        }
        
        String taskResult = "CPU密集型任务完成: " + taskName + 
                           ", 计算结果: " + result + 
                           ", 执行线程: " + Thread.currentThread().getName();
        log.info("CPU密集型任务完成: {}", taskResult);
        
        return CompletableFuture.completedFuture(taskResult);
    }

    /**
     * 模拟网络请求的异步方法
     */
    @Async("ioTaskExecutor")
    public CompletableFuture<String> simulateNetworkRequest(String url) {
        log.info("开始模拟网络请求: {}, 线程: {}", url, Thread.currentThread().getName());
        
        try {
            // 模拟网络延迟
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("网络请求被中断: {}", url);
            return CompletableFuture.completedFuture("网络请求被中断: " + url);
        }
        
        String response = "网络请求完成: " + url + ", 响应时间: 1秒, 线程: " + Thread.currentThread().getName();
        log.info("网络请求完成: {}", response);
        
        return CompletableFuture.completedFuture(response);
    }

    /**
     * 模拟数据库操作的异步方法
     */
    @Async("ioTaskExecutor")
    public CompletableFuture<String> simulateDatabaseOperation(String operation) {
        log.info("开始模拟数据库操作: {}, 线程: {}", operation, Thread.currentThread().getName());
        
        try {
            // 模拟数据库操作延迟
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("数据库操作被中断: {}", operation);
            return CompletableFuture.completedFuture("数据库操作被中断: " + operation);
        }
        
        String result = "数据库操作完成: " + operation + ", 执行线程: " + Thread.currentThread().getName();
        log.info("数据库操作完成: {}", result);
        
        return CompletableFuture.completedFuture(result);
    }

    /**
     * 批量异步任务
     * 演示如何同时执行多个异步任务
     */
    @Async
    public CompletableFuture<String> batchAsyncTasks(int taskCount) {
        log.info("开始执行批量异步任务, 任务数量: {}, 线程: {}", taskCount, Thread.currentThread().getName());
        
        try {
            // 模拟批量处理
            for (int i = 0; i < taskCount; i++) {
                TimeUnit.MILLISECONDS.sleep(100);
                log.info("处理子任务 {}/{}", i + 1, taskCount);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("批量任务被中断");
            return CompletableFuture.completedFuture("批量任务被中断");
        }
        
        String result = "批量异步任务完成, 处理了 " + taskCount + " 个任务, 线程: " + Thread.currentThread().getName();
        log.info("批量异步任务完成: {}", result);
        
        return CompletableFuture.completedFuture(result);
    }
}
