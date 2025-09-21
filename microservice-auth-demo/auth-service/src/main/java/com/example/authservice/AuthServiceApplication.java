package com.example.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证服务应用程序主类
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("🔐 认证服务启动成功！");
        System.out.println("📍 服务地址: http://localhost:8081");
        System.out.println("📋 健康检查: http://localhost:8081/actuator/health");
        System.out.println("🎯 登录接口: http://localhost:8081/auth/login");
    }
}
