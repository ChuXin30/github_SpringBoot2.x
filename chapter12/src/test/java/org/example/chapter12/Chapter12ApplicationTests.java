package org.example.chapter12;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Spring Security 示例应用程序测试类
 */
@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.org.springframework.security=WARN",
    "spring.security.user.name=",
    "spring.security.user.password=",
    "spring.security.user.roles="
})
class Chapter12ApplicationTests {

    @Test
    void contextLoads() {
        // 测试 Spring 上下文是否能正常加载
    }
}
