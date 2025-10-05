package com.hanyahunya.auth.adapter.out.grpc;

import com.hanyahunya.auth.application.port.out.SocialLoginPort;
import com.hanyahunya.auth.domain.model.Provider;
import google_login.GoogleLoginServiceGrpc;
import google_login.LoginRequest;
import google_login.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleLoginAdapter implements SocialLoginPort {

    private final GoogleLoginServiceGrpc.GoogleLoginServiceBlockingStub googleLoginStub;

    @Override
    public String processLogin(String validateCode) {
        LoginRequest request = LoginRequest.newBuilder()
                .setAuthorizationCode(validateCode)
                .build();
        // todo 고유값 가져와서 이미 있는 쌍인지 확인후 있으면 해당 user_id로 토큰발급후 로그인 처리 없으면 회원가입 처리후 로그인 처리
        try {
            // 2. integration-service에 gRPC 요청을 보내고 응답을 받음 (동기 방식)
            LoginResponse response = googleLoginStub.googleLogin(request);

            String userSub = response.getSub();

            // 3. 받은 sub 값으로 우리 서비스의 DB에서 유저 조회 또는 회원가입 처리
            // ... (auth-service의 핵심 로직) ...

            // 4. 우리 서비스의 JWT 토큰 발급 후 반환
            // return jwtTokenProvider.createToken(userSub);

            return userSub; // 임시로 sub 반환

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
