package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务应用程序主类
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("👤 用户服务启动成功！");
        System.out.println("📍 服务地址: http://localhost:8082");
        System.out.println("📋 健康检查: http://localhost:8082/actuator/health");
        System.out.println("🎯 用户资料: http://localhost:8082/user/profile");
    }
}
