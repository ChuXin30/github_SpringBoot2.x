package org.example.chapter13;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 多线程示例应用启动类
 * 
 * @author example
 * @since 1.0.0
 */
@SpringBootApplication
public class Chapter13Application {

    public static void main(String[] args) {
        SpringApplication.run(Chapter13Application.class, args);
        System.out.println("Spring Boot 多线程示例应用启动成功！");
        System.out.println("访问地址: http://localhost:8080");
    }
}
