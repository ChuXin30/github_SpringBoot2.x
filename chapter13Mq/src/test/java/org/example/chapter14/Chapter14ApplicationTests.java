package org.example.chapter14;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring Boot ActiveMQ 示例应用测试类
 * 
 * @author example
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test") // 激活测试配置文件
class Chapter14ApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
        // 在测试环境中，JMS功能被禁用，所以不会尝试连接ActiveMQ
    }
}
