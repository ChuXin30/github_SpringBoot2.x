package com.example.chapter11.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello 控制器示例
 * 提供基本的 REST API 接口
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    /**
     * 简单的问候接口
     */
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, Spring Boot!");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "success");
        return response;
    }

    /**
     * 带参数的问候接口
     */
    @GetMapping("/hello/{name}")
    public Map<String, Object> helloWithName(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, " + name + "!");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "success");
        return response;
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Chapter11 Demo");
        return response;
    }
}
