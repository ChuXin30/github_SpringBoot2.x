package com.example.chapter11.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Hello 控制器测试类
 */
@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHello() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, Spring Boot!"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testHelloWithName() throws Exception {
        mockMvc.perform(get("/api/hello/张三"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, 张三!"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Chapter11 Demo"));
    }
}
