package org.example.rust.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户模型类
 * 使用不可变设计，类似于Rust的结构体
 */
public class User {
    
    @JsonProperty("id")
    private final Long id;
    
    @JsonProperty("name")
    private final String name;
    
    @JsonProperty("email")
    private final String email;
    
    @JsonProperty("age")
    private final Integer age;
    
    @JsonProperty("created_at")
    private final LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private final LocalDateTime updatedAt;
    
    // 私有构造函数，使用Builder模式
    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }
    
    // MyBatis需要的构造函数
    public User(Long id, String name, String email, Integer age, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // 创建新的Builder
    public static Builder builder() {
        return new Builder();
    }
    
    // 创建副本并修改某些字段
    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .age(this.age)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(name, user.name) &&
               Objects.equals(email, user.email) &&
               Objects.equals(age, user.age);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    /**
     * Builder模式实现
     */
    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private Integer age;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder age(Integer age) {
            this.age = age;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public User build() {
            return new User(this);
        }
    }
}
