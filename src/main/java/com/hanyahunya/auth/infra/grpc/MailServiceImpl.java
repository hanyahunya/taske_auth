package com.hanyahunya.auth.infra.grpc;

import com.hanyahunya.auth.application.port.out.MailServicePort; // 변경
import email_service.EmailRequest;
import email_service.EmailResponse;
import email_service.EmailServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailServicePort { // 변경

    private final EmailServiceGrpc.EmailServiceBlockingStub emailStub;

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
        Map<String, String> variables = new HashMap<>();
        variables.put("verification_link", verificationLink);

        sendMail(email, subjectKey, templateName, locale, variables);
    }

    // sendMail 메서드도 locale을 파라미터로 받도록 수정
    private void sendMail(String to, String subjectKey, String templateName, String locale, Map<String, String> variables) {
        log.info("Attempting to send email. To: {}, Template: {}, Locale: {}", to, templateName, locale);

        try {
            EmailRequest request = EmailRequest.newBuilder()
                    .setTo(to)
                    .setSubject(subjectKey)
                    .setTemplateName(templateName)
                    .setLocale(locale)
                    .putAllVariables(variables)
                    .build();

            EmailResponse response = emailStub.sendEmail(request);

            if (response.getSuccess()) {
                log.info("Successfully sent email to {}", to);
            } else {
                log.error("Failed to send email to {}: {}", to, response.getMessage());
            }
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while sending email to {}: {}", to, e.getStatus());
        }
    }
}