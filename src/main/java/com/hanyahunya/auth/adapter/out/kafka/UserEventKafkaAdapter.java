package com.hanyahunya.auth.adapter.out.kafka;

import com.hanyahunya.auth.application.port.out.UserEventPublishPort;
import com.hanyahunya.auth.domain.model.User;
import com.hanyahunya.kafkaDto.UserSignedUpEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventKafkaAdapter implements UserEventPublishPort {

    private final KafkaTemplate<String, UserSignedUpEvent> kafkaTemplate;
    private static final String USER_EVENTS_TOPIC = "user-events";

    @Override
    public void publishUserSignedUpEvent(UserSignedUpEvent event) {
        /*
            가운데 매개변수 (user_id는 토픽의 어느 파티션에서 처리할지 정하는 역할.
            -설정시-
                > 같은 토픽에서 동일한 키가 있으면 파티션을 원래 키가 있던 파티션에 queue 형식으로 메시지를 넣음
                -> 순서가 보장됨. ex) 회원가입 -> 회원정보 변경이 같은 토픽, 같은 키면 회원가입 후에 정보변경이 보장됨.
            -미설정시-
                > 위와 동일한 상황에서 같은 토픽이여도 파티션이 나뉘기에 A파티션에 회원가입 이벤트가있고, B파티션에 정보변경 이벤트가 있을때 B가 처리속도가 빨라서 B가 먼저 실행되버릴수 있음 -> 오류
                > 토픽이 다르면 딱히 키는 상관없지만 너무 잘게 이벤트별로 나누는거보단 도메인별로 나누는게 좋아보임.
                -> 그럼 처리는 어떻게 하냐? 같은 user-events topic으로 발행. Consumer에서 @KafkaListener(topics = "user-events" ...) @KafkaHandler로 라우팅 방식으로 처리!
         */
        kafkaTemplate.send(USER_EVENTS_TOPIC, event.getUserId().toString(), event);
    }
}
