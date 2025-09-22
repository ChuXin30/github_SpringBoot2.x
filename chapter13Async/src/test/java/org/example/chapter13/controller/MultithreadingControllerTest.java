package org.example.chapter13.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.chapter13.service.AsyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * MultithreadingController 单元测试类
 * 测试多线程控制器的各种API接口
 * 
 * @author example
 * @since 1.0.0
 */
@WebMvcTest(MultithreadingController.class)
class MultithreadingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AsyncService asyncService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 设置Mock行为
        when(asyncService.asyncTaskWithResult(anyString()))
                .thenReturn(CompletableFuture.completedFuture("任务完成: 测试任务, 执行线程: Async-1"));
        
        when(asyncService.ioIntensiveTask(anyString()))
                .thenReturn(CompletableFuture.completedFuture("I/O密集型任务完成: 测试任务, 执行线程: IO-Async-1"));
        
        when(asyncService.cpuIntensiveTask(anyString()))
                .thenReturn(CompletableFuture.completedFuture("CPU密集型任务完成: 测试任务, 计算结果: 123456, 执行线程: CPU-Async-1"));
        
        when(asyncService.simulateNetworkRequest(anyString()))
                .thenReturn(CompletableFuture.completedFuture("网络请求完成: http://test.com, 响应时间: 1秒, 线程: IO-Async-1"));
        
        when(asyncService.simulateDatabaseOperation(anyString()))
                .thenReturn(CompletableFuture.completedFuture("数据库操作完成: SELECT * FROM test, 执行线程: IO-Async-1"));
        
        when(asyncService.batchAsyncTasks(anyInt()))
                .thenReturn(CompletableFuture.completedFuture("批量异步任务完成, 处理了 5 个任务, 线程: Async-1"));
    }

    @Test
    @DisplayName("测试基本异步任务API")
    void testBasicAsyncTask() throws Exception {
        mockMvc.perform(post("/api/multithreading/basic-async")
                .param("taskName", "测试任务"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("基本异步任务已提交"))
                .andExpect(jsonPath("$.taskName").value("测试任务"))
                .andExpect(jsonPath("$.note").value("任务在后台异步执行，请查看日志"));
    }

    @Test
    @DisplayName("测试带返回值的异步任务API")
    void testAsyncTaskWithResult() throws Exception {
        mockMvc.perform(post("/api/multithreading/async-with-result")
                .param("taskName", "带返回值任务"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("任务完成: 测试任务, 执行线程: Async-1"))
                .andExpect(jsonPath("$.taskName").value("带返回值任务"))
                .andExpect(jsonPath("$.executionTime").exists());
    }

    @Test
    @DisplayName("测试I/O密集型任务API")
    void testIoIntensiveTask() throws Exception {
        mockMvc.perform(post("/api/multithreading/io-intensive")
                .param("taskName", "I/O密集型任务"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("I/O密集型任务完成: 测试任务, 执行线程: IO-Async-1"))
                .andExpect(jsonPath("$.taskType").value("I/O密集型"))
                .andExpect(jsonPath("$.executionTime").exists());
    }

    @Test
    @DisplayName("测试CPU密集型任务API")
    void testCpuIntensiveTask() throws Exception {
        mockMvc.perform(post("/api/multithreading/cpu-intensive")
                .param("taskName", "CPU密集型任务"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("CPU密集型任务完成: 测试任务, 计算结果: 123456, 执行线程: CPU-Async-1"))
                .andExpect(jsonPath("$.taskType").value("CPU密集型"))
                .andExpect(jsonPath("$.executionTime").exists());
    }

    @Test
    @DisplayName("测试并发网络请求API")
    void testConcurrentRequests() throws Exception {
        // 设置Mock行为
        List<CompletableFuture<String>> futures = Arrays.asList(
                CompletableFuture.completedFuture("网络请求完成: http://api.example.com/data/0, 响应时间: 1秒, 线程: IO-Async-1"),
                CompletableFuture.completedFuture("网络请求完成: http://api.example.com/data/1, 响应时间: 1秒, 线程: IO-Async-2"),
                CompletableFuture.completedFuture("网络请求完成: http://api.example.com/data/2, 响应时间: 1秒, 线程: IO-Async-3")
        );
        
        when(asyncService.simulateNetworkRequest(anyString()))
                .thenReturn(futures.get(0), futures.get(1), futures.get(2));

        mockMvc.perform(post("/api/multithreading/concurrent-requests")
                .param("requestCount", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestCount").value(3))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(3))
                .andExpect(jsonPath("$.totalExecutionTime").exists())
                .andExpect(jsonPath("$.averageTimePerRequest").exists());
    }

    @Test
    @DisplayName("测试并发数据库操作API")
    void testConcurrentDbOperations() throws Exception {
        // 设置Mock行为
        List<CompletableFuture<String>> futures = Arrays.asList(
                CompletableFuture.completedFuture("数据库操作完成: SELECT * FROM users WHERE id = 0, 执行线程: IO-Async-1"),
                CompletableFuture.completedFuture("数据库操作完成: SELECT * FROM users WHERE id = 1, 执行线程: IO-Async-2"),
                CompletableFuture.completedFuture("数据库操作完成: SELECT * FROM users WHERE id = 2, 执行线程: IO-Async-3")
        );
        
        when(asyncService.simulateDatabaseOperation(anyString()))
                .thenReturn(futures.get(0), futures.get(1), futures.get(2));

        mockMvc.perform(post("/api/multithreading/concurrent-db-operations")
                .param("operationCount", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationCount").value(3))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(3))
                .andExpect(jsonPath("$.totalExecutionTime").exists())
                .andExpect(jsonPath("$.averageTimePerOperation").exists());
    }

    @Test
    @DisplayName("测试批量异步任务API")
    void testBatchTasks() throws Exception {
        mockMvc.perform(post("/api/multithreading/batch-tasks")
                .param("taskCount", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("批量异步任务完成, 处理了 5 个任务, 线程: Async-1"))
                .andExpect(jsonPath("$.taskCount").value(10))
                .andExpect(jsonPath("$.executionTime").exists());
    }

    @Test
    @DisplayName("测试混合任务类型API")
    void testMixedTasks() throws Exception {
        mockMvc.perform(post("/api/multithreading/mixed-tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ioTaskResult").exists())
                .andExpect(jsonPath("$.cpuTaskResult").exists())
                .andExpect(jsonPath("$.networkTaskResult").exists())
                .andExpect(jsonPath("$.totalExecutionTime").exists())
                .andExpect(jsonPath("$.note").value("不同类型的任务使用不同的线程池执行"));
    }

    @Test
    @DisplayName("测试获取系统信息API")
    void testGetSystemInfo() throws Exception {
        mockMvc.perform(get("/api/multithreading/system-info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableProcessors").exists())
                .andExpect(jsonPath("$.maxMemory").exists())
                .andExpect(jsonPath("$.totalMemory").exists())
                .andExpect(jsonPath("$.freeMemory").exists())
                .andExpect(jsonPath("$.currentThread").exists());
    }

    @Test
    @DisplayName("测试API参数验证")
    void testApiParameterValidation() throws Exception {
        // 测试默认参数
        mockMvc.perform(post("/api/multithreading/basic-async"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskName").value("测试任务"));

        // 测试自定义参数
        mockMvc.perform(post("/api/multithreading/basic-async")
                .param("taskName", "自定义任务名称"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskName").value("自定义任务名称"));
    }

    @Test
    @DisplayName("测试API响应格式")
    void testApiResponseFormat() throws Exception {
        mockMvc.perform(post("/api/multithreading/async-with-result")
                .param("taskName", "响应格式测试"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").isString())
                .andExpect(jsonPath("$.executionTime").isString())
                .andExpect(jsonPath("$.taskName").isString());
    }

    @Test
    @DisplayName("测试API错误处理")
    void testApiErrorHandling() throws Exception {
        // 模拟服务异常
        when(asyncService.asyncTaskWithResult(anyString()))
                .thenThrow(new RuntimeException("模拟异常"));

        mockMvc.perform(post("/api/multithreading/async-with-result")
                .param("taskName", "异常测试"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("测试API性能")
    void testApiPerformance() throws Exception {
        long startTime = System.currentTimeMillis();

        mockMvc.perform(get("/api/multithreading/system-info"))
                .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // 系统信息API应该很快返回
        assert executionTime < 1000 : "系统信息API响应时间应该小于1秒";
    }
}
