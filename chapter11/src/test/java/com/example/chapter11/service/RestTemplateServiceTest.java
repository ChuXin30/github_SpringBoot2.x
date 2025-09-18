package com.example.chapter11.service;

import com.example.chapter11.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RestTemplate 服务测试类
 */
@SpringBootTest
@TestPropertySource(properties = {
        "logging.level.com.example.chapter11=DEBUG"
})
class RestTemplateServiceTest {

    @Autowired
    private RestTemplateService restTemplateService;

    @Test
    void testGetUserById() {
        // 测试获取用户ID为1的用户
        User user = restTemplateService.getUserById(1L);
        
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertNotNull(user.getName());
        assertNotNull(user.getEmail());
        
        System.out.println("获取的用户: " + user);
    }

    @Test
    void testGetAllUsers() {
        // 测试获取所有用户
        List<User> users = restTemplateService.getAllUsers();
        
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.size() > 0);
        
        System.out.println("用户总数: " + users.size());
        System.out.println("第一个用户: " + users.get(0));
    }

    @Test
    void testCreateUser() {
        // 测试创建用户
        User newUser = new User("测试用户", "test@example.com", "123-456-7890", "test.com");
        User createdUser = restTemplateService.createUser(newUser);
        
        assertNotNull(createdUser);
        assertEquals(newUser.getName(), createdUser.getName());
        assertEquals(newUser.getEmail(), createdUser.getEmail());
        assertEquals(newUser.getPhone(), createdUser.getPhone());
        assertEquals(newUser.getWebsite(), createdUser.getWebsite());
        
        System.out.println("创建的用户: " + createdUser);
    }

    @Test
    void testGetUserWithCustomHeaders() {
        // 测试使用自定义HTTP头获取用户
        User user = restTemplateService.getUserWithCustomHeaders(1L);
        
        assertNotNull(user);
        assertEquals(1L, user.getId());
        
        System.out.println("使用自定义头获取的用户: " + user);
    }

    @Test
    void testGetSimpleData() {
        // 测试使用简单RestTemplate获取数据
        String data = restTemplateService.getSimpleData();
        
        assertNotNull(data);
        assertFalse(data.isEmpty());
        
        System.out.println("获取的简单数据: " + data.substring(0, Math.min(200, data.length())) + "...");
    }

    @Test
    void testGetUserWithErrorHandling() {
        // 测试错误处理 - 使用不存在的用户ID
        User user = restTemplateService.getUserWithErrorHandling(999L);
        
        // 由于使用的是测试API，可能会返回null或抛出异常
        // 这里主要测试错误处理逻辑不会导致程序崩溃
        System.out.println("错误处理测试结果: " + user);
    }
}
