package org.example.rust;

import org.example.rust.model.User;
import org.example.rust.service.RustStyleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Rust风格接口测试
 */
@SpringBootTest
public class RustStyleTest {
    
    private final RustStyleService rustStyleService = new RustStyleService();
    
    @Test
    public void testResultOk() {
        Result<String, AppError> result = Result.ok("成功");
        
        assertTrue(result.isOk());
        assertFalse(result.isErr());
        assertEquals("成功", result.unwrap());
        assertEquals("成功", result.unwrapOr("默认值"));
    }
    
    @Test
    public void testResultErr() {
        Result<String, AppError> result = Result.err(AppError.VALIDATION_ERROR);
        
        assertFalse(result.isOk());
        assertTrue(result.isErr());
        assertEquals(AppError.VALIDATION_ERROR, result.unwrapErr());
        assertEquals("默认值", result.unwrapOr("默认值"));
    }
    
    @Test
    public void testResultMap() {
        Result<String, AppError> result = Result.ok("hello");
        Result<String, AppError> mapped = result.map(String::toUpperCase);
        
        assertTrue(mapped.isOk());
        assertEquals("HELLO", mapped.unwrap());
    }
    
    @Test
    public void testResultAndThen() {
        Result<String, AppError> result = Result.ok("hello");
        Result<String, AppError> chained = result.andThen(value -> 
            Result.ok(value.toUpperCase()));
        
        assertTrue(chained.isOk());
        assertEquals("HELLO", chained.unwrap());
    }
    
    @Test
    public void testOptionSome() {
        Option<String> option = Option.some("有值");
        
        assertTrue(option.isSome());
        assertFalse(option.isNone());
        assertEquals("有值", option.unwrap());
        assertEquals("有值", option.unwrapOr("默认值"));
    }
    
    @Test
    public void testOptionNone() {
        Option<String> option = Option.none();
        
        assertFalse(option.isSome());
        assertTrue(option.isNone());
        assertEquals("默认值", option.unwrapOr("默认值"));
    }
    
    @Test
    public void testOptionFromNullable() {
        Option<String> some = Option.fromNullable("有值");
        Option<String> none = Option.fromNullable(null);
        
        assertTrue(some.isSome());
        assertTrue(none.isNone());
    }
    
    @Test
    public void testOptionMap() {
        Option<String> option = Option.some("hello");
        Option<String> mapped = option.map(String::toUpperCase);
        
        assertTrue(mapped.isSome());
        assertEquals("HELLO", mapped.unwrap());
    }
    
    @Test
    public void testUserCreation() {
        User user = User.builder()
            .name("测试用户")
            .email("test@example.com")
            .age(25)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        assertEquals("测试用户", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(25, user.getAge());
    }
    
    @Test
    public void testUserValidation() {
        // 测试有效用户
        User validUser = User.builder()
            .name("有效用户")
            .email("valid@example.com")
            .age(25)
            .build();
        
        Result<User, AppError> result = rustStyleService.createUser(validUser);
        assertTrue(result.isOk());
        
        // 测试无效邮箱
        User invalidUser = User.builder()
            .name("无效用户")
            .email("invalid-email")
            .age(25)
            .build();
        
        Result<User, AppError> invalidResult = rustStyleService.createUser(invalidUser);
        assertTrue(invalidResult.isErr());
        assertEquals(AppError.VALIDATION_ERROR, invalidResult.unwrapErr());
    }
    
    @Test
    public void testUserRetrieval() {
        // 创建用户
        User user = User.builder()
            .name("查询测试")
            .email("query@example.com")
            .age(30)
            .build();
        
        Result<User, AppError> createResult = rustStyleService.createUser(user);
        assertTrue(createResult.isOk());
        
        User createdUser = createResult.unwrap();
        Long userId = createdUser.getId();
        
        // 查询用户
        Option<User> foundUser = rustStyleService.getUserById(userId);
        assertTrue(foundUser.isSome());
        assertEquals("查询测试", foundUser.unwrap().getName());
        
        // 查询不存在的用户
        Option<User> notFound = rustStyleService.getUserById(999L);
        assertTrue(notFound.isNone());
    }
    
    @Test
    public void testErrorContext() {
        AppError error = AppError.VALIDATION_ERROR
            .withContext("field", "email")
            .withMessage("邮箱格式不正确");
        
        assertEquals("VALIDATION_ERROR", error.getCode());
        assertTrue(error.getContext().containsKey("field"));
        assertEquals("email", error.getContext().get("field"));
    }
}
