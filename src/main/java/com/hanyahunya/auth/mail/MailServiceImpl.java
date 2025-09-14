package com.hanyahunya.auth.mail;

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
public class MailServiceImpl implements MailService {

    private final EmailServiceGrpc.EmailServiceBlockingStub emailStub;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String email, String verificationCode) {
        String subject = "会員登録を完了するには、メールを認証してください。";
        String verificationLink = frontendUrl + "/verify/" + verificationCode;

        Map<String, String> variables = new HashMap<>();
        variables.put("verification_link", verificationLink);

        sendMail(email, subject, "verification-template", variables);
    }

    /**
     * メール送信の実際のgRPC通信を担当するプライベートメソッドです。
     * @param to 受信者のメールアドレス
     * @param subject 件名
     * @param templateName 使用するメールテンプレート名
     * @param variables テンプレートに注入される変数マップ
     */
    private void sendMail(String to, String subject, String templateName, Map<String, String> variables) {
        log.info("Attempting to send email. To: {}, Subject: {}", to, subject);

        try {
            EmailRequest request = EmailRequest.newBuilder()
                    .setTo(to)
                    .setSubject(subject)
                    .setIsHtml(false)
                    .setContent(templateName)
                    .putAllVariables(variables)
                    .build();

            EmailResponse response = emailStub.sendEmail(request);

            // 응답 처리
            if (response.getSuccess()) {
                log.info("Successfully sent email to {}", to);
            } else {
                log.error("Failed to send email to {}: {}", to, response.getMessage());
                // throw new EmailSendFailedException(response.getMessage());
            }
        } catch (StatusRuntimeException e) {
            // gRPC通信自体でエラーが発生した場合
            log.error("gRPC error while sending email to {}: {}", to, e.getStatus());
        }
    }
}