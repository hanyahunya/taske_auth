package com.hanyahunya.auth.application.port.in;

import java.util.UUID;

public interface TokenCompromiseUseCase {
    /**
     * 토큰탈취에 대한 후속 조치를 처리
     * @param userId 탈취가 의심되는 userId
     */
    void handleCompromise(UUID userId);
}
