package com.hanyahunya.auth.adapter.out.grpc;

import com.hanyahunya.auth.application.port.out.ProcessSocialAuthPort;
import com.hanyahunya.auth.domain.model.Provider;
import lombok.RequiredArgsConstructor;
import social_login.LoginRequest;
import social_login.SocialLoginServiceGrpc;

@RequiredArgsConstructor
public class GrpcGoogleAuthAdapter implements ProcessSocialAuthPort {

    private final SocialLoginServiceGrpc.SocialLoginServiceBlockingStub socialLoginStub;

    @Override
    public void processLogin(String validateCode, String socialSub) {
        LoginRequest request = LoginRequest.newBuilder()
                .setProvider(Provider.GOOGLE.name())
                .setAuthorizationCode(validateCode)
                .build();
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
