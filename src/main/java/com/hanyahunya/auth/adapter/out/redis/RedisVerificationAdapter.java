package com.hanyahunya.auth.adapter.out.redis;

import com.hanyahunya.auth.application.port.out.VerificationPort;
import com.hanyahunya.auth.domain.util.RandomString;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisVerificationAdapter implements VerificationPort {

    private final StringRedisTemplate redisTemplate;
    private static final String VERIFICATION_KEY_PREFIX = "auth-service:signup:verify:";

    @Override
    public String createVerificationCode(UUID userId) {
        String verificationCode = RandomString.generate(200);
        redisTemplate.opsForValue().set(
                VERIFICATION_KEY_PREFIX + verificationCode,
                userId.toString(),
                1, TimeUnit.HOURS
        );
        return verificationCode;
    }

    @Override
    public Optional<String> getUserIdByVerificationCode(String verificationCode) {
        String userId = redisTemplate.opsForValue().get(VERIFICATION_KEY_PREFIX + verificationCode);
        return Optional.ofNullable(userId);
    }

    @Override
    public void deleteVerificationCode(String verificationCode) {
        redisTemplate.delete(VERIFICATION_KEY_PREFIX + verificationCode);
    }
}