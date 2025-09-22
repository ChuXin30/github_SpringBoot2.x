package org.example.chapter13Mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

/**
 * Spring Boot ActiveMQ 示例应用启动类
 * 
 * @author example
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJms  // 启用JMS支持
public class Chapter14Application {

    public static void main(String[] args) {
        SpringApplication.run(Chapter14Application.class, args);
        System.out.println("Spring Boot ActiveMQ 示例应用启动成功！");
        System.out.println("访问地址: http://localhost:8080");
        System.out.println("ActiveMQ Web控制台: http://localhost:8161/admin");
    }
}
