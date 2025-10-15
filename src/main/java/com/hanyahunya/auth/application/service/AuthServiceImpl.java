package com.hanyahunya.auth.application.service;

import com.hanyahunya.auth.application.command.LoginCommand;
import com.hanyahunya.auth.application.command.SocialLoginCommand;
import com.hanyahunya.auth.application.command.ValidateTfaCommand;
import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.in.AuthService;
import com.hanyahunya.auth.application.port.in.TokenService;
import com.hanyahunya.auth.application.port.out.*;
import com.hanyahunya.auth.application.command.SignupCommand;
import com.hanyahunya.auth.application.security.IdTokenValidator;
import com.hanyahunya.auth.application.security.IdTokenValidatorFactory;
import com.hanyahunya.auth.domain.exception.*;
import com.hanyahunya.auth.domain.model.*;
import com.hanyahunya.auth.domain.repository.SocialAccountRepository;
import com.hanyahunya.auth.domain.repository.UserRepository;
import com.hanyahunya.auth.domain.util.RandomString;
import com.hanyahunya.kafkaDto.UserSignedUpEvent;
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
    private final MailServicePort mailServicePort;
    private final UserEventPublishPort userEventPublishPort;
    private final TokenService tokenService;
    private final VerifyTokenPort verifyTokenPort;
    private final SocialLoginAdapterFactory socialLoginAdapterFactory;
    private final SocialAccountRepository socialAccountRepository;
    private final IdTokenValidatorFactory idTokenValidatorFactory;

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
        mailServicePort.sendVerificationEmail(email, verificationCode, locale);
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
        User savedUser = userRepository.save(user);
        userEventPublishPort.publishUserSignedUpEvent(UserSignedUpEvent.fromUser(savedUser));
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
            case COMPROMISED -> {
                String token = verifyTokenPort.issueToken(user.getEmail());
                String verifyCode = RandomString.generateNumeric(6);
                mailServicePort.sendVerificationCode(user.getEmail(), verifyCode, user.getCountry());
                verificationPort.saveSecondFactorCode(user.getEmail(), verifyCode);
                throw new UserCompromisedException("追加の認証が必要です。メールに認証コードを送信しました。", token);
            }
            case PENDING_VERIFICATION ->
                    throw new UserPendingVerificationException("先にメール認証を済ましてください。");
        };
    }

    @Override
    @Transactional
    public Tokens validateTfa(ValidateTfaCommand command) {
        if (!verificationPort.verifySecondFactorCode(command.email(), command.validateCode())) {
            throw new InvalidVerificationCodeException();
        }
        verificationPort.deleteSecondFactorCode(command.email());

        return userRepository.findByEmail(command.email())
                .map(user -> {
                    user.updateStatus(Status.ACTIVE);
                    return tokenService.loginAndIssueTokens(user);
                }).orElseThrow(LoginFailedException::new);
    }
    
    @Transactional
    @Override
    public Tokens socialLogin(SocialLoginCommand command) {
        Provider provider = command.provider();
        ProcessSocialAuthPort processSocialAuthPort = socialLoginAdapterFactory.getAdapter(provider);
        IdTokenValidator idTokenValidator = idTokenValidatorFactory.getIdTokenValidator(provider);

        String sub = idTokenValidator.validateAndGetSub(command.idToken(), command.nonce());
        return socialAccountRepository.findByProviderAndProviderId(provider, sub)
                .map(socialAccount -> tokenService.loginAndIssueTokens(socialAccount.getUser()))
                .orElseGet(() -> {
                    UUID userId = UUID.randomUUID();
                    User user = User.builder()
                            .userId(userId)
                            .country(command.locale())
                            .role(Role.ROLE_USER)
                            .status(Status.ACTIVE)
                            .build();
                    SocialAccount socialAccount = SocialAccount.builder()
                            .provider(provider)
                            .providerId(sub)
                            .user(user)
                            .build();

                    socialAccountRepository.save(socialAccount);
                    userEventPublishPort.publishUserSignedUpEvent(UserSignedUpEvent.fromUser(user));
                    processSocialAuthPort.processLogin(command.validateCode(), sub);

                    return tokenService.loginAndIssueTokens(user);
                });
    }
}
