package com.hanyahunya.auth.application.port.out;

import java.util.UUID;

public interface AccessLockPort {
    void lock(UUID userId, long timeoutMinutes);

    void unlock(UUID userId);
}
