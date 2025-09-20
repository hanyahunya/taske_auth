package com.hanyahunya.auth.application.service;

import com.hanyahunya.auth.adapter.in.web.dto.LoginDto;
import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.in.AuthService;
import com.hanyahunya.auth.application.port.in.TokenService;
import com.hanyahunya.auth.application.port.out.EncodeServicePort;
import com.hanyahunya.auth.application.port.out.MailServicePort;
import com.hanyahunya.auth.adapter.in.web.dto.SignupDto;
import com.hanyahunya.auth.application.port.out.VerificationPort;
import com.hanyahunya.auth.domain.exception.EmailAlreadyExistsException;
import com.hanyahunya.auth.domain.exception.InvalidTokenException;
import com.hanyahunya.auth.domain.exception.InvalidVerificationCodeException;
import com.hanyahunya.auth.domain.exception.ResourceNotFoundException;
import com.hanyahunya.auth.domain.model.Role;
import com.hanyahunya.auth.domain.model.Status;
import com.hanyahunya.auth.domain.model.User;
import com.hanyahunya.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final EncodeServicePort encodeService;
    private final UserRepository userRepository;
    private final VerificationPort verificationPort;
    private final MailServicePort mailService;

    @Transactional
    @Override
    public void signUp(SignupDto signupDto) {
        userRepository.findByEmail(signupDto.getEmail())
                .ifPresentOrElse(
                user -> {
                    if (user.getStatus() == Status.PENDING_VERIFICATION) {
                        user.updateTimestamp();
                        userRepository.save(user);
                        initiateEmailVerification(user.getUserId(), user.getEmail(), signupDto.getLocale());
                    } else {
                        throw new EmailAlreadyExistsException();
                    }
                },
                () -> {
                    signupDto.setPassword(encodeService.encode(signupDto.getPassword()));
                    UUID uuid = UUID.randomUUID();
                    User user = User.builder()
                            .userId(uuid)
                            .email(signupDto.getEmail())
                            .password(signupDto.getPassword())
                            .role(Role.ROLE_USER)
                            .status(Status.PENDING_VERIFICATION)
                            .build();
                    userRepository.save(user);
                    initiateEmailVerification(uuid, user.getEmail(), signupDto.getLocale());
                }
        );
    }

    private void initiateEmailVerification(UUID userId, String email, String locale) {
        String verificationCode = verificationPort.createVerificationCode(userId);
        mailService.sendVerificationEmail(email, verificationCode, locale);
    }

    @Transactional
    @Override
    public void completeSignup(String verificationCode) {
        String uuid = verificationPort.getUserIdByVerificationCode(verificationCode)
                .orElseThrow(InvalidVerificationCodeException::new);
        int updated = userRepository.updateUserStatus(UUID.fromString(uuid), Status.ACTIVE.name());
        if (updated == 0) {
            throw new ResourceNotFoundException("user_id", uuid);
        }
        verificationPort.deleteVerificationCode(verificationCode);
    }

    @Override
    public void cleanupUnverifiedUsers() {
        LocalDateTime threshold = LocalDateTime.now().toLocalDate().atTime(3, 0);
        userRepository.deleteByStatusAndUpdatedAtBefore(Status.PENDING_VERIFICATION, threshold);
    }

    @Override
    public Tokens login(LoginDto loginDto) {
        return null;
    }
}
