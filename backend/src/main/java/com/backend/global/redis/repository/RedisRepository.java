package com.backend.global.redis.repository;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value, long duration, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, duration, timeUnit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void remove(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key); //Redis에 key가 존재 하는지
    }

    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value); //Redis에 key, value 저장
    }

}
