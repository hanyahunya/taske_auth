package com.hanyahunya.auth.domain.service;

import com.hanyahunya.auth.application.port.in.AuthService;
import com.hanyahunya.auth.application.port.out.EncodeServicePort;
import com.hanyahunya.auth.application.port.out.MailServicePort;
import com.hanyahunya.auth.application.web.dto.SignupDto;
import com.hanyahunya.auth.domain.exception.EmailAlreadyExistsException;
import com.hanyahunya.auth.domain.exception.InvalidTokenException;
import com.hanyahunya.auth.domain.exception.ResourceNotFoundException;
import com.hanyahunya.auth.domain.model.Role;
import com.hanyahunya.auth.domain.model.Status;
import com.hanyahunya.auth.domain.model.User;
import com.hanyahunya.auth.domain.repository.UserRepository;
import com.hanyahunya.auth.domain.util.RandomString;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final EncodeServicePort encodeService;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final MailServicePort mailService;

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
                .password(signupDto.getPassword())
                .role(Role.USER)
                .status(Status.PENDING_VERIFICATION)
                .build();
        userRepository.save(user);

        String verificationCode = RandomString.generate(200);
        redisTemplate.opsForValue().set(VERIFICATION_KEY_PREFIX + verificationCode, uuid.toString(), 1, TimeUnit.HOURS);

        mailService.sendVerificationEmail(user.getEmail(), verificationCode, signupDto.getLocale());
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
