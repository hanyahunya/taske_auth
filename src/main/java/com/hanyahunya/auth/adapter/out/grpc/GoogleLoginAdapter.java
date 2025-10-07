package com.hanyahunya.auth.adapter.out.grpc;

import com.hanyahunya.auth.application.port.out.SocialLoginPort;
import com.hanyahunya.auth.domain.model.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import social_login.LoginRequest;
import social_login.SocialLoginServiceGrpc;

@Service
@RequiredArgsConstructor
public class GoogleLoginAdapter implements SocialLoginPort {

    private final SocialLoginServiceGrpc.SocialLoginServiceBlockingStub socialLoginStub;

    @Override
    public void processLogin(String validateCode) {
        LoginRequest request = LoginRequest.newBuilder()
                .setProvider(Provider.GOOGLE.name())
                .setAuthorizationCode(validateCode)
                .build();
        // todo 고유값 가져와서 이미 있는 쌍인지 확인후 있으면 해당 user_id로 토큰발급후 로그인 처리 없으면 회원가입 처리후 로그인 처리
        try {
            socialLoginStub.socialLogin(request);

        } catch (Exception e) {
            // gRPC 통신 오류 또는 integration-service에서 발생한 에러 처리
            throw new RuntimeException("Failed to process google login: " + e.getMessage(), e);
        }
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }
}
