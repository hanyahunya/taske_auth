package com.hanyahunya.auth.application.port.out;

import com.hanyahunya.kafkaDto.UserSignedUpEvent;

public interface UserEventPublishPort {
    void publishUserSignedUpEvent(UserSignedUpEvent event);
}
