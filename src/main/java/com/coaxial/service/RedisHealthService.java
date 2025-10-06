package com.coaxial.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.coaxial.dto.HealthResponse.DatabaseStatus;

@Service
@ConditionalOnProperty(name = "spring.data.redis.enabled", havingValue = "true", matchIfMissing = false)
public class RedisHealthService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public DatabaseStatus checkRedisConnectivity() {
        try {
            // Test Redis connection by setting and getting a test key
            String testKey = "health_check:" + System.currentTimeMillis();
            String testValue = "test";
            
            redisTemplate.opsForValue().set(testKey, testValue, java.time.Duration.ofSeconds(5));
            String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);
            
            if (testValue.equals(retrievedValue)) {
                // Clean up test key
                redisTemplate.delete(testKey);
                return new DatabaseStatus("UP", "Redis connection successful");
            } else {
                return new DatabaseStatus("DOWN", "Redis test failed - value mismatch");
            }
        } catch (Exception e) {
            return new DatabaseStatus("DOWN", "Redis connection failed: " + e.getMessage());
        }
    }
}
