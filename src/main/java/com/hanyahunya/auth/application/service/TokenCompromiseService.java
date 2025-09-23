package com.hanyahunya.auth.application.service;

import com.hanyahunya.auth.application.port.in.TokenCompromiseUseCase;
import com.hanyahunya.auth.application.port.out.AccessLockPort;
import com.hanyahunya.auth.application.port.out.SecurityNotificationPort;
import com.hanyahunya.auth.domain.model.Status;
import com.hanyahunya.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCompromiseService implements TokenCompromiseUseCase {

    private final AccessLockPort accessLockPort;
    private final UserRepository userRepository;
    private final SecurityNotificationPort securityNotificationPort;

    @Override
    @Transactional
    public void handleCompromise(UUID userId) {
        log.info("ユーザーID '{}' のトークン侵害を処理しています", userId);

        accessLockPort.lock(userId, 15);

        userRepository.findById(userId).ifPresent(user -> {
            user.updateStatus(Status.COMPROMISED);
//            userRepository.save(user); // Dirty Checking

            // todo 추후 유저 닉네임, 로캘 정보 추가
            securityNotificationPort.sendCompromiseNotification(user.getEmail(), LocalDateTime.now(), "ja-JP");

            log.info("ユーザーID '{}' のトークン侵害処理が正常に完了しました。", userId);
        });
    }
}
