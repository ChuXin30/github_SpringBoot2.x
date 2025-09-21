package com.example.userservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis缓存配置
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // 默认10分钟过期
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // 不同缓存的个性化配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // 用户资料缓存 - 10分钟
        cacheConfigurations.put("user-profiles", 
            defaultConfig.entryTtl(Duration.ofMinutes(10)));
            
        // 用户名查询缓存 - 5分钟
        cacheConfigurations.put("user-profiles-by-username", 
            defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // 部门用户列表 - 30分钟
        cacheConfigurations.put("department-users", 
            defaultConfig.entryTtl(Duration.ofMinutes(30)));
            
        // 角色用户列表 - 15分钟
        cacheConfigurations.put("role-users", 
            defaultConfig.entryTtl(Duration.ofMinutes(15)));
            
        // 最近活跃用户 - 2分钟 (更新频繁)
        cacheConfigurations.put("recent-users", 
            defaultConfig.entryTtl(Duration.ofMinutes(2)));
            
        // 部门统计 - 1小时
        cacheConfigurations.put("department-stats", 
            defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
