package com.example.chapter11.controller;

import com.example.chapter11.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RestTemplate 控制器测试类
 */
@SpringBootTest
@AutoConfigureWebMvc
class RestTemplateControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private MockMvc getMockMvc() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }
        return mockMvc;
    }

    @Test
    void testGetHelp() throws Exception {
        getMockMvc().perform(get("/api/resttemplate/help"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetUser() throws Exception {
        getMockMvc().perform(get("/api/resttemplate/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetAllUsers() throws Exception {
        getMockMvc().perform(get("/api/resttemplate/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User("测试用户", "test@example.com", "123-456-7890", "test.com");
        String userJson = objectMapper.writeValueAsString(user);

        getMockMvc().perform(post("/api/resttemplate/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User("更新用户", "update@example.com", "987-654-3210", "update.com");
        String userJson = objectMapper.writeValueAsString(user);

        getMockMvc().perform(put("/api/resttemplate/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void testDeleteUser() throws Exception {
        getMockMvc().perform(delete("/api/resttemplate/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void testGetUserWithCustomHeaders() throws Exception {
        getMockMvc().perform(get("/api/resttemplate/user/1/custom-headers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetUsersWithParams() throws Exception {
        getMockMvc().perform(get("/api/resttemplate/users/params")
                        .param("name", "测试")
                        .param("age", "25"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetUserWithExchange() throws Exception {
        getMockMvc().perform(get("/api/resttemplate/user/1/exchange"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetUserWithErrorHandling() throws Exception {
        getMockMvc().perform(get("/api/resttemplate/user/999/error-handling"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetSimpleData() throws Exception {
        getMockMvc().perform(get("/api/resttemplate/simple-data"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").exists());
    }
}
