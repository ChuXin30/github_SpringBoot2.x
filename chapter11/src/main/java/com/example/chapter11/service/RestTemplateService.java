package com.example.chapter11.service;

import com.example.chapter11.model.ApiResponse;
import com.example.chapter11.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RestTemplate 服务类
 * 演示RestTemplate的各种用法
 */
@Service
public class RestTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("simpleRestTemplate")
    private RestTemplate simpleRestTemplate;

    // 模拟API基础URL
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private static final String LOCAL_API_URL = "http://localhost:8080/api";

    /**
     * GET请求 - 获取单个用户
     */
    public User getUserById(Long id) {
        String url = BASE_URL + "/users/" + id;
        logger.info("GET请求 - 获取用户ID: {}", id);
        
        try {
            User user = restTemplate.getForObject(url, User.class);
            logger.info("获取用户成功: {}", user);
            return user;
        } catch (Exception e) {
            logger.error("获取用户失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * GET请求 - 获取用户列表
     */
    public List<User> getAllUsers() {
        String url = BASE_URL + "/users";
        logger.info("GET请求 - 获取所有用户");
        
        try {
            User[] users = restTemplate.getForObject(url, User[].class);
            List<User> userList = Arrays.asList(users);
            logger.info("获取用户列表成功，共{}个用户", userList.size());
            return userList;
        } catch (Exception e) {
            logger.error("获取用户列表失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * GET请求 - 使用ResponseEntity
     */
    public ResponseEntity<User> getUserWithResponseEntity(Long id) {
        String url = BASE_URL + "/users/" + id;
        logger.info("GET请求 - 使用ResponseEntity获取用户ID: {}", id);
        
        try {
            ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);
            logger.info("响应状态码: {}", response.getStatusCode());
            logger.info("响应头: {}", response.getHeaders());
            logger.info("响应体: {}", response.getBody());
            return response;
        } catch (Exception e) {
            logger.error("获取用户失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * POST请求 - 创建用户
     */
    public User createUser(User user) {
        String url = BASE_URL + "/users";
        logger.info("POST请求 - 创建用户: {}", user);
        
        try {
            User createdUser = restTemplate.postForObject(url, user, User.class);
            logger.info("创建用户成功: {}", createdUser);
            return createdUser;
        } catch (Exception e) {
            logger.error("创建用户失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * POST请求 - 使用ResponseEntity
     */
    public ResponseEntity<User> createUserWithResponseEntity(User user) {
        String url = BASE_URL + "/users";
        logger.info("POST请求 - 使用ResponseEntity创建用户: {}", user);
        
        try {
            ResponseEntity<User> response = restTemplate.postForEntity(url, user, User.class);
            logger.info("创建用户响应状态码: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            logger.error("创建用户失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * PUT请求 - 更新用户
     */
    public void updateUser(Long id, User user) {
        String url = BASE_URL + "/users/" + id;
        logger.info("PUT请求 - 更新用户ID: {}, 用户信息: {}", id, user);
        
        try {
            restTemplate.put(url, user);
            logger.info("更新用户成功");
        } catch (Exception e) {
            logger.error("更新用户失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * DELETE请求 - 删除用户
     */
    public void deleteUser(Long id) {
        String url = BASE_URL + "/users/" + id;
        logger.info("DELETE请求 - 删除用户ID: {}", id);
        
        try {
            restTemplate.delete(url);
            logger.info("删除用户成功");
        } catch (Exception e) {
            logger.error("删除用户失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 使用自定义HTTP头
     */
    public User getUserWithCustomHeaders(Long id) {
        String url = BASE_URL + "/users/" + id;
        logger.info("GET请求 - 使用自定义HTTP头获取用户ID: {}", id);
        
        // 创建HTTP头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "RestTemplate-Demo/1.0");
        headers.set("Accept", "application/json");
        
        // 创建HttpEntity
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<User> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    entity, 
                    User.class
            );
            logger.info("使用自定义头获取用户成功: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            logger.error("使用自定义头获取用户失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 使用URL参数
     */
    public List<User> getUsersWithParams(String name, Integer age) {
        String url = BASE_URL + "/users?name={name}&age={age}";
        logger.info("GET请求 - 使用URL参数获取用户，name: {}, age: {}", name, age);
        
        // 创建参数Map
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("age", age);
        
        try {
            User[] users = restTemplate.getForObject(url, User[].class, params);
            List<User> userList = Arrays.asList(users);
            logger.info("使用参数获取用户列表成功，共{}个用户", userList.size());
            return userList;
        } catch (Exception e) {
            logger.error("使用参数获取用户列表失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 使用exchange方法进行复杂请求
     */
    public ResponseEntity<ApiResponse<User>> getUserWithExchange(Long id) {
        String url = BASE_URL + "/users/" + id;
        logger.info("EXCHANGE请求 - 获取用户ID: {}", id);
        
        // 创建HTTP头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // 创建HttpEntity
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // 创建响应类型引用
        ParameterizedTypeReference<ApiResponse<User>> responseType = 
                new ParameterizedTypeReference<ApiResponse<User>>() {};
        
        try {
            ResponseEntity<ApiResponse<User>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    responseType
            );
            logger.info("EXCHANGE请求成功，状态码: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            logger.error("EXCHANGE请求失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 错误处理示例
     */
    public User getUserWithErrorHandling(Long id) {
        String url = BASE_URL + "/users/" + id;
        logger.info("GET请求 - 带错误处理获取用户ID: {}", id);
        
        try {
            User user = restTemplate.getForObject(url, User.class);
            logger.info("获取用户成功: {}", user);
            return user;
        } catch (Exception e) {
            logger.error("获取用户失败，错误类型: {}, 错误信息: {}", 
                    e.getClass().getSimpleName(), e.getMessage());
            
            // 根据不同的异常类型进行不同的处理
            if (e.getMessage().contains("404")) {
                logger.warn("用户不存在，ID: {}", id);
                return null;
            } else if (e.getMessage().contains("500")) {
                logger.error("服务器内部错误");
                throw new RuntimeException("服务器暂时不可用，请稍后重试");
            } else {
                logger.error("未知错误");
                throw new RuntimeException("请求失败: " + e.getMessage());
            }
        }
    }

    /**
     * 使用简单RestTemplate的示例
     */
    public String getSimpleData() {
        String url = "https://httpbin.org/get";
        logger.info("使用简单RestTemplate获取数据");
        
        try {
            String result = simpleRestTemplate.getForObject(url, String.class);
            logger.info("获取数据成功");
            return result;
        } catch (Exception e) {
            logger.error("获取数据失败: {}", e.getMessage());
            throw e;
        }
    }
}
