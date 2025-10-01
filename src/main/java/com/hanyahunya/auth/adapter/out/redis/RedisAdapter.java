package com.hanyahunya.auth.adapter.out.redis;

import com.hanyahunya.auth.application.command.SignupCommand;
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
    public String createTemporaryUser(String email, String password, String locale) {
        String verificationCode = RandomString.generateAlphanumeric(200);
        String tempUserKey = TEMP_USER_KEY_PREFIX + verificationCode;
        String cooldownKey = COOLDOWN_KEY_PREFIX + email;

        SignupCommand signupCommand = new SignupCommand(email, password, locale);

        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi(); // Transaction 시작

                // 실행할 작업들
                operations.opsForValue().set((K) tempUserKey, (V) signupCommand, 1, TimeUnit.HOURS);
                operations.opsForValue().set((K) cooldownKey, (V) "locked", 1, TimeUnit.HOURS);

                return operations.exec(); // Transaction 실행
            }
        });

        return verificationCode;
    }

    /*
        todo 아래거 해결. 일단 기능개발
            gateway         -request->      adapter(in)     Dto
            adapter(in)     -request->      useCase         command
            useCase         -request->      adapter(out)    params
            adapter(out)    -response->     useCase         command (????)
            useCase         -response->     adapter(in)     ???
     */
    @Override
    public Optional<SignupCommand> findTemporaryUserByCode(String verificationCode) {
        String key = TEMP_USER_KEY_PREFIX + verificationCode;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof SignupCommand signupCommand) {
            return Optional.of(signupCommand);
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

        if (value instanceof SignupCommand signupCommand) {
            String email = signupCommand.email();
            String cooldownKey = COOLDOWN_KEY_PREFIX + email;

            redisTemplate.delete(List.of(tempUserKey, cooldownKey));

        } else {
            redisTemplate.delete(tempUserKey);
        }
    }

    // 2fa
    private final String TFA_CODE_KEY_PREFIX = "auth:2fa:email:";
    @Override
    public void saveSecondFactorCode(String email, String code) {
        String key = TFA_CODE_KEY_PREFIX + email;

        stringRedisTemplate.opsForValue().set(key, code, 3, TimeUnit.MINUTES);
    }

    @Override
    public boolean verifySecondFactorCode(String email, String submittedCode) {
        String key = TFA_CODE_KEY_PREFIX + email;

        String storedCode = stringRedisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return false;
        }

        return storedCode.equals(submittedCode);
    }

    @Override
    public void deleteSecondFactorCode(String email) {
        String key = TFA_CODE_KEY_PREFIX + email;
        stringRedisTemplate.delete(key);
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