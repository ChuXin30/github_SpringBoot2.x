package org.example.chapter13.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.chapter13.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 多线程演示控制器
 * 提供各种多线程使用场景的API接口
 * 
 * @author example
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/multithreading")
public class MultithreadingController {

    private static final Logger log = LoggerFactory.getLogger(MultithreadingController.class);

    @Autowired
    private AsyncService asyncService;

    /**
     * 基本异步任务演示
     */
    @PostMapping("/basic-async")
    public Map<String, Object> basicAsyncTask(@RequestParam(defaultValue = "测试任务") String taskName) {
        log.info("接收到基本异步任务请求: {}", taskName);
        
        long startTime = System.currentTimeMillis();
        
        // 执行异步任务
        asyncService.basicAsyncTask(taskName);
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "基本异步任务已提交");
        result.put("taskName", taskName);
        result.put("submitTime", endTime - startTime + "ms");
        result.put("note", "任务在后台异步执行，请查看日志");
        
        return result;
    }

    /**
     * 带返回值的异步任务演示
     */
    @PostMapping("/async-with-result")
    public Map<String, Object> asyncTaskWithResult(@RequestParam(defaultValue = "带返回值任务") String taskName) 
            throws ExecutionException, InterruptedException {
        log.info("接收到带返回值的异步任务请求: {}", taskName);
        
        long startTime = System.currentTimeMillis();
        
        // 执行异步任务并等待结果
        CompletableFuture<String> future = asyncService.asyncTaskWithResult(taskName);
        String result = future.get();
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("executionTime", endTime - startTime + "ms");
        response.put("taskName", taskName);
        
        return response;
    }

    /**
     * I/O密集型任务演示
     */
    @PostMapping("/io-intensive")
    public Map<String, Object> ioIntensiveTask(@RequestParam(defaultValue = "I/O密集型任务") String taskName) 
            throws ExecutionException, InterruptedException {
        log.info("接收到I/O密集型任务请求: {}", taskName);
        
        long startTime = System.currentTimeMillis();
        
        CompletableFuture<String> future = asyncService.ioIntensiveTask(taskName);
        String result = future.get();
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("executionTime", endTime - startTime + "ms");
        response.put("taskType", "I/O密集型");
        
        return response;
    }

    /**
     * CPU密集型任务演示
     */
    @PostMapping("/cpu-intensive")
    public Map<String, Object> cpuIntensiveTask(@RequestParam(defaultValue = "CPU密集型任务") String taskName) 
            throws ExecutionException, InterruptedException {
        log.info("接收到CPU密集型任务请求: {}", taskName);
        
        long startTime = System.currentTimeMillis();
        
        CompletableFuture<String> future = asyncService.cpuIntensiveTask(taskName);
        String result = future.get();
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("executionTime", endTime - startTime + "ms");
        response.put("taskType", "CPU密集型");
        
        return response;
    }

    /**
     * 并发网络请求演示
     */
    @PostMapping("/concurrent-requests")
    public Map<String, Object> concurrentRequests(@RequestParam(defaultValue = "5") int requestCount) 
            throws ExecutionException, InterruptedException {
        log.info("接收到并发网络请求，数量: {}", requestCount);
        
        long startTime = System.currentTimeMillis();
        
        // 创建多个并发请求
        List<CompletableFuture<String>> futures = IntStream.range(0, requestCount)
                .mapToObj(i -> asyncService.simulateNetworkRequest("http://api.example.com/data/" + i))
                .collect(Collectors.toList());
        
        // 等待所有请求完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );
        
        allFutures.get();
        
        // 收集所有结果
        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("requestCount", requestCount);
        response.put("totalExecutionTime", endTime - startTime + "ms");
        response.put("averageTimePerRequest", (endTime - startTime) / requestCount + "ms");
        
        return response;
    }

    /**
     * 并发数据库操作演示
     */
    @PostMapping("/concurrent-db-operations")
    public Map<String, Object> concurrentDbOperations(@RequestParam(defaultValue = "3") int operationCount) 
            throws ExecutionException, InterruptedException {
        log.info("接收到并发数据库操作请求，数量: {}", operationCount);
        
        long startTime = System.currentTimeMillis();
        
        // 创建多个并发数据库操作
        List<CompletableFuture<String>> futures = IntStream.range(0, operationCount)
                .mapToObj(i -> asyncService.simulateDatabaseOperation("SELECT * FROM users WHERE id = " + i))
                .collect(Collectors.toList());
        
        // 等待所有操作完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );
        
        allFutures.get();
        
        // 收集所有结果
        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("operationCount", operationCount);
        response.put("totalExecutionTime", endTime - startTime + "ms");
        response.put("averageTimePerOperation", (endTime - startTime) / operationCount + "ms");
        
        return response;
    }

    /**
     * 批量异步任务演示
     */
    @PostMapping("/batch-tasks")
    public Map<String, Object> batchTasks(@RequestParam(defaultValue = "10") int taskCount) 
            throws ExecutionException, InterruptedException {
        log.info("接收到批量异步任务请求，任务数量: {}", taskCount);
        
        long startTime = System.currentTimeMillis();
        
        CompletableFuture<String> future = asyncService.batchAsyncTasks(taskCount);
        String result = future.get();
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("taskCount", taskCount);
        response.put("executionTime", endTime - startTime + "ms");
        
        return response;
    }

    /**
     * 混合任务类型演示
     * 同时执行I/O密集型和CPU密集型任务
     */
    @PostMapping("/mixed-tasks")
    public Map<String, Object> mixedTasks() throws ExecutionException, InterruptedException {
        log.info("接收到混合任务类型请求");
        
        long startTime = System.currentTimeMillis();
        
        // 同时执行不同类型的任务
        CompletableFuture<String> ioTask = asyncService.ioIntensiveTask("混合I/O任务");
        CompletableFuture<String> cpuTask = asyncService.cpuIntensiveTask("混合CPU任务");
        CompletableFuture<String> networkTask = asyncService.simulateNetworkRequest("http://api.example.com/mixed");
        
        // 等待所有任务完成
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(ioTask, cpuTask, networkTask);
        allTasks.get();
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("ioTaskResult", ioTask.get());
        response.put("cpuTaskResult", cpuTask.get());
        response.put("networkTaskResult", networkTask.get());
        response.put("totalExecutionTime", endTime - startTime + "ms");
        response.put("note", "不同类型的任务使用不同的线程池执行");
        
        return response;
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/system-info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("maxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");
        info.put("totalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB");
        info.put("freeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB");
        info.put("currentThread", Thread.currentThread().getName());
        
        return info;
    }
}
