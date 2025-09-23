package com.hanyahunya.auth.application.port.out;

import java.time.LocalDateTime;

public interface SecurityNotificationPort {
    /**
     * 토큰 탈취 의심 알림 메일을 전송
     * @param email 전송 대상 이메일
     */
    void sendCompromiseNotification(String email, LocalDateTime compromisedAt, String locale); // todo 추후 user 서비스에서 유저의 정보를 불러와 (닉네임, 국가등) 담아서 전송.
}
