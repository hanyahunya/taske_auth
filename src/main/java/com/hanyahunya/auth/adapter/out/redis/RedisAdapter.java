package com.hanyahunya.auth.adapter.out.redis;

import com.hanyahunya.auth.adapter.in.web.dto.SignupDto;
import com.hanyahunya.auth.application.port.out.AccessLockPort;
import com.hanyahunya.auth.application.port.out.VerificationPort;
import com.hanyahunya.auth.domain.util.RandomString;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisAdapter implements VerificationPort, AccessLockPort {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private final String TEMP_USER_KEY_PREFIX = "auth:signup:temp_user:";
    private final String COOLDOWN_KEY_PREFIX = "auth:signup:cooldown:";
    @Override
    public String createTemporaryUser(SignupDto signupDto) {
        String verificationCode = RandomString.generate(200);
        String tempUserKey = TEMP_USER_KEY_PREFIX + verificationCode;
        String cooldownKey = COOLDOWN_KEY_PREFIX + signupDto.getEmail();
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi(); // Transaction 시작

                // 실행할 작업들
                operations.opsForValue().set((K) tempUserKey, (V) signupDto, 1, TimeUnit.HOURS);
                operations.opsForValue().set((K) cooldownKey, (V) "locked", 1, TimeUnit.HOURS);

                return operations.exec(); // Transaction 실행
            }
        });

        return verificationCode;
    }

    @Override
    public Optional<SignupDto> findTemporaryUserByCode(String verificationCode) {
        String key = TEMP_USER_KEY_PREFIX + verificationCode;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof SignupDto signupDto) {
            return Optional.of(signupDto);
        }
        return Optional.empty();
    }

    @Override
    public boolean isCooldown(String email) {
        String key = COOLDOWN_KEY_PREFIX + email;
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public void deleteVerificationCode(String verificationCode) {
        String tempUserKey = TEMP_USER_KEY_PREFIX + verificationCode;

        Object value = redisTemplate.opsForValue().get(tempUserKey);

        if (value instanceof SignupDto signupDto) {
            String email = signupDto.getEmail();
            String cooldownKey = COOLDOWN_KEY_PREFIX + email;

            redisTemplate.delete(List.of(tempUserKey, cooldownKey));

        } else {
            redisTemplate.delete(tempUserKey);
        }
    }

    private static final String ACCESS_LOCK_KEY_PREFIX = "blacklist:user:";
    @Override
    public void lock(UUID userId, long compromisedAt) {
        stringRedisTemplate.opsForValue().set(
                ACCESS_LOCK_KEY_PREFIX + userId.toString(),
                String.valueOf(compromisedAt),
                15, TimeUnit.MINUTES);
    }

//    @Override
//    public void unlock(UUID userId) {
//        redisTemplate.delete(ACCESS_LOCK_KEY_PREFIX + userId.toString());
//    }
}