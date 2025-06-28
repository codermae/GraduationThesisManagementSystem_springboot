// TopicCacheConfig.java - 缓存配置（可选）
package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class TopicCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setAllowNullValues(false);
        // 设置缓存名称
        cacheManager.setCacheNames(java.util.Arrays.asList(
                "topics",           // 题目缓存
                "topicSelections",  // 选题记录缓存
                "teacherTopics"     // 教师题目缓存
        ));
        return cacheManager;
    }
}