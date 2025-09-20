package com.hanyahunya.auth.adapter.in.scheduler;

import com.hanyahunya.auth.application.port.in.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCleanUpScheduler {
    private final AuthService authService;

    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanupPendingUsers() {
        authService.cleanupUnverifiedUsers();
    }
}