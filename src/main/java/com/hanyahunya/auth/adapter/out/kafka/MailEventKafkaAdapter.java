package com.hanyahunya.auth.adapter.out.kafka;

import com.hanyahunya.auth.application.port.out.MailServicePort;
import com.hanyahunya.auth.application.port.out.SecurityNotificationPort;
import com.hanyahunya.kafkaDto.SendSystemMailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailEventKafkaAdapter implements MailServicePort, SecurityNotificationPort {

    private static final String SYSTEM_MAIL_TOPIC = "system-mail-events";
    private final KafkaTemplate<String, SendSystemMailEvent> kafkaTemplate;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String email, String verificationCode, String locale) {
        // 제목을 실제 텍스트가 아닌, worker 서비스의 properties 파일에 있는 '키(key)'로 지정 <- 추후 바꿔야할수도?
        String subjectKey = "email.verification.title";
        // 템플릿의 논리적인 이름 지정
        String templateName = "auth-verification";
        // 템플릿에 전달할 변수 생성
        String verificationLink = frontendUrl + "/verify/" + verificationCode;
        String cancelLink = frontendUrl + "/verify/cancel/" + verificationCode;
        Map<String, String> variables = new HashMap<>();
        variables.put("verification_link", verificationLink);
        variables.put("cancel_link", cancelLink);

        sendMail(email, subjectKey, templateName, locale, variables);
    }

    @Override
    public void sendVerificationCode(String email, String verificationCode, String locale) {
        String subjectKey = "email.tfa.title";
        String templateName = "auth-tfa";
        Map<String, String> variables = new HashMap<>();
        variables.put("verification_code", verificationCode);

        sendMail(email, subjectKey, templateName, locale, variables);
    }

    @Override
    public void sendCompromiseNotification(String email, LocalDateTime compromisedAt, String locale) {
        String subjectKey = "email.test.title";
        String templateName = "auth-test";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, String> variables = new HashMap<>();
        variables.put("compromise_time", compromisedAt.format(formatter));

        sendMail(email, subjectKey, templateName, locale, variables);
    }

    private void sendMail(String to, String subjectKey, String templateName, String locale, Map<String, String> variables) {
        log.info("Attempting to send email. To: {}, Template: {}, Locale: {}", to, templateName, locale);

        SendSystemMailEvent event = SendSystemMailEvent.builder()
                .to(to)
                .subject(subjectKey)
                .templateName(templateName)
                .locale(locale)
                .variables(variables)
                .build();

        // todo 전송 실패시. acknowledgement? 암튼 그거 기반 실패 처리 필요 ***중요***
        kafkaTemplate.send(SYSTEM_MAIL_TOPIC, to, event);
    }
}
