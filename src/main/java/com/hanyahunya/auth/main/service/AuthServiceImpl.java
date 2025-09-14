package com.hanyahunya.auth.main.service;

import com.hanyahunya.auth.exception.InvalidTokenException;
import com.hanyahunya.auth.exception.ResourceNotFoundException;
import com.hanyahunya.auth.mail.MailService;
import com.hanyahunya.auth.main.dto.SignupDto;
import com.hanyahunya.auth.main.entity.Status;
import com.hanyahunya.auth.util.RandomString;
import com.hanyahunya.auth.util.encoding.EncodeService;
import com.hanyahunya.auth.main.entity.Role;
import com.hanyahunya.auth.main.entity.User;
import com.hanyahunya.auth.exception.EmailAlreadyExistsException;
import com.hanyahunya.auth.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final EncodeService encodeService;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final MailService mailService;

    private static final String VERIFICATION_KEY_PREFIX = "auth-service:signup:verify:";

    @Transactional
    @Override
    public void signUp(SignupDto signupDto) {
        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        signupDto.setPassword(encodeService.encode(signupDto.getPassword()));
        UUID uuid = UUID.randomUUID();
        User user = User.builder()
                .userId(uuid)
                .email(signupDto.getEmail())
                .password(encodeService.encode(signupDto.getPassword()))
                .role(Role.USER)
                .status(Status.PENDING_VERIFICATION)
                .build();
        userRepository.save(user);

        String verificationCode = RandomString.generate(200);
        redisTemplate.opsForValue().set(VERIFICATION_KEY_PREFIX + verificationCode, uuid.toString(), 1, TimeUnit.HOURS);

        mailService.sendVerificationEmail(user.getEmail(), verificationCode);
    }

    @Transactional
    @Override
    public void completeSignup(String verificationCode) {
        String uuid = redisTemplate.opsForValue().get(VERIFICATION_KEY_PREFIX + verificationCode);
        if (uuid == null) {
            throw new InvalidTokenException();
        }
        int updated = userRepository.updateUserStatus(UUID.fromString(uuid), Status.ACTIVE.name());
        if (updated == 0) {
            throw new ResourceNotFoundException("user_id", uuid);
        }
        redisTemplate.delete(VERIFICATION_KEY_PREFIX + verificationCode);
    }
}
