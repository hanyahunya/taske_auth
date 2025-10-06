package com.hanyahunya.auth.application.port.out;

import java.util.UUID;

public interface AccessLockPort {
    void lock(UUID userId, long compromisedAt);

//    void unlock(UUID userId);
}
