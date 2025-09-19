package org.example.chapter12.controller;

import org.example.chapter12.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API 控制器测试类
 */
@WebMvcTest(ApiController.class)
@Import(TestSecurityConfig.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPublicApi() throws Exception {
        mockMvc.perform(get("/api/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("这是一个公开的 API 端点"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testProtectedApiWithUser() throws Exception {
        mockMvc.perform(get("/api/protected"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("这是一个受保护的 API 端点"))
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAdminApiWithAdmin() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("这是一个管理员 API 端点"))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAdminApiWithUser() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isForbidden());
    }
}
