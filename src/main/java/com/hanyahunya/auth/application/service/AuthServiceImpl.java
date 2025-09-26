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
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException();
                });
        if (verificationPort.isCooldown(signupDto.getEmail())) {
            throw new VerificationCooldownException("認証メールはすでに送信されています。受信したメールをご確認ください。");
        }
        signupDto.setPassword(encodeService.encode(signupDto.getPassword()));
        String verificationCode = verificationPort.createTemporaryUser(signupDto);
        mailService.sendVerificationEmail(signupDto.getEmail(), verificationCode, signupDto.getLocale());
    }

    @Transactional
    @Override
    public void completeSignup(String verificationCode) {
        SignupDto dto = verificationPort.findTemporaryUserByCode(verificationCode)
                .orElseThrow(InvalidVerificationCodeException::new);

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .userId(userId)
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.ROLE_USER)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);
        // todo Kafka 유저 가입 이벤트 발급
        verificationPort.deleteVerificationCode(verificationCode);
    }

    @Override
    public Tokens login(LoginDto loginDto) {
        return null;
    }
}
