package com.hanyahunya.auth.application.service;

import com.hanyahunya.auth.adapter.in.web.dto.LoginDto;
import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.in.AuthService;
import com.hanyahunya.auth.application.port.in.TokenService;
import com.hanyahunya.auth.application.port.out.EncodeServicePort;
import com.hanyahunya.auth.application.port.out.MailServicePort;
import com.hanyahunya.auth.adapter.in.web.dto.SignupDto;
import com.hanyahunya.auth.application.port.out.VerificationPort;
import com.hanyahunya.auth.domain.exception.*;
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
        String dtoEmail = signupDto.getEmail();
        String dtoEncodedPassword = encodeService.encode(signupDto.getPassword());
        String dtoLocale = signupDto.getLocale();
        userRepository.findByEmail(signupDto.getEmail())
                // ifPresentOrElse는 작업 수행만, return값 없음. 반환필요사 .map().orElse()
                .ifPresentOrElse(
                user -> {
                    if (user.getStatus() == Status.PENDING_VERIFICATION) {
                        if (user.getUpdatedAt().isBefore(LocalDateTime.now().minusHours(1))) {
                            user.updateTimestamp();
                            user.updatePassword(dtoEncodedPassword);
                            initiateEmailVerification(user.getUserId(), user.getEmail(), dtoLocale);
                        } else {
                            throw new VerificationCooldownException("認証メールはすでに送信されています。受信したメールから認証を完了してください。");
                        }
                    } else {
                        throw new EmailAlreadyExistsException();
                    }
                },
                () -> {
                    UUID userId = UUID.randomUUID();
                    User user = User.builder()
                            .userId(userId)
                            .email(dtoEmail)
                            .password(dtoEncodedPassword)
                            .role(Role.ROLE_USER)
                            .status(Status.PENDING_VERIFICATION)
                            .build();
                    userRepository.save(user);
                    initiateEmailVerification(userId, dtoEmail, dtoLocale);
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
