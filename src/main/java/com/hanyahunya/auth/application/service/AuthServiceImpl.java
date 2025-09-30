package com.hanyahunya.auth.application.service;

import com.hanyahunya.auth.adapter.in.web.dto.LoginDto;
import com.hanyahunya.auth.application.command.LoginCommand;
import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.in.AuthService;
import com.hanyahunya.auth.application.port.in.TokenService;
import com.hanyahunya.auth.application.port.out.*;
import com.hanyahunya.auth.application.command.SignupCommand;
import com.hanyahunya.auth.domain.exception.*;
import com.hanyahunya.auth.domain.model.Role;
import com.hanyahunya.auth.domain.model.Status;
import com.hanyahunya.auth.domain.model.User;
import com.hanyahunya.auth.domain.repository.UserRepository;
import com.hanyahunya.kafkaDto.UserSignedUpEvent;
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
    private final UserEventPublishPort userEventPublishPort;
    private final TokenService tokenService;

    @Transactional
    @Override
    public void signUp(SignupCommand command) {
        String email = command.email();
        String encodedPassword = encodeService.encode(command.password());
        String locale = command.locale();

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException();
                });
        if (verificationPort.isCooldown(email)) {
            throw new VerificationCooldownException("認証メールはすでに送信されています。受信したメールをご確認ください。");
        }
        String verificationCode = verificationPort.createTemporaryUser(email, encodedPassword, locale);
        mailService.sendVerificationEmail(email, verificationCode, locale);
    }

    @Transactional
    @Override
    public void completeSignup(String verificationCode) {
        SignupCommand command = verificationPort.findTemporaryUserByCode(verificationCode)
                .orElseThrow(InvalidVerificationCodeException::new);

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .userId(userId)
                .email(command.email())
                .password(command.password())
                // 회원가입시에만 이렇게 저장하고, 정보 변경시에는 kafka 이벤트로 처리
                .country(command.locale())
                .role(Role.ROLE_USER)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);
        UserSignedUpEvent userSignedUpEvent = UserSignedUpEvent.builder()
                .userId(userId)
                .email(command.email())
                .country(command.locale())
                .signedUpAt(LocalDateTime.now())
                .build();
        userEventPublishPort.publishUserSignedUpEvent(userSignedUpEvent);
        verificationPort.deleteVerificationCode(verificationCode);
    }

    @Override
    public Tokens login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(LoginFailedException::new);
        if (!encodeService.matches(command.password(), user.getPassword())) {
            throw new LoginFailedException();
        }
        return switch (user.getStatus()) {
            case ACTIVE -> tokenService.loginAndIssueTokens(user);
            // todo 임시 토큰 발급(새로 + purpose) 3분 claims-email, redis에 key: ~~~:2fa:~~~@~~.com value:6int, 인증후에 해당 유저 로그인처리.
            case COMPROMISED -> {
                throw new RuntimeException("<UNK>");
            }
            // todo 이메일 인증후에 로그인 해달라고 요청.(이건 프론트 엔드에서)
            case PENDING_VERIFICATION -> {
                throw new RuntimeException("<UdNK>");
            }
        };
    }
}
