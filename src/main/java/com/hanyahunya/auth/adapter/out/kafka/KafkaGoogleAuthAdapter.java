package com.hanyahunya.auth.adapter.out.kafka;

import com.hanyahunya.auth.application.port.out.ProcessSocialAuthPort;
import com.hanyahunya.auth.domain.model.Provider;
import com.hanyahunya.kafkaDto.SocialAuthCodeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaGoogleAuthAdapter implements ProcessSocialAuthPort {

    private final KafkaTemplate<String, SocialAuthCodeEvent> kafkaTemplate;
    private static final String SOCIAL_AUTH_TOPIC_PREFIX = "social-auth";

    @Override
    public void processLogin(String validateCode, String socialSub) {
        SocialAuthCodeEvent event = SocialAuthCodeEvent.builder()
                .provider(Provider.GOOGLE.name())
                .authorizationCode(validateCode)
                .build();
        kafkaTemplate.send(SOCIAL_AUTH_TOPIC_PREFIX + ".code.received", socialSub, event);
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }
}
