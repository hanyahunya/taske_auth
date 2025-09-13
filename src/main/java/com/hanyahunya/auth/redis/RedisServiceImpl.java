package com.hanyahunya.auth.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(String key, Object value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (JsonProcessingException e) {
            log.error("値のJSONシリアライズに失敗しました。 key: {}, value: {}", key, value, e);
        }
    }

    @Override
    public void save(String key, Object value, long expirationInSeconds) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, Duration.ofSeconds(expirationInSeconds));
        } catch (JsonProcessingException e) {
            log.error("値のJSONシリアライズに失敗しました。 key: {}, value: {}", key, value, e);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        String jsonValue = redisTemplate.opsForValue().get(key);
        if (jsonValue == null) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonValue, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSONからのデシリアライズに失敗しました。 key: {}, jsonValue: {}", key, jsonValue, e);
            return null;
        }
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}