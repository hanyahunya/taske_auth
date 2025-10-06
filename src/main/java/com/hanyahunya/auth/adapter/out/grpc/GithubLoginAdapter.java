package com.hanyahunya.auth.adapter.out.grpc;

import com.hanyahunya.auth.application.port.out.SocialLoginPort;
import com.hanyahunya.auth.domain.model.Provider;
import org.springframework.stereotype.Service;

@Service
public class GithubLoginAdapter implements SocialLoginPort {
    @Override
    public String processLogin(String validateCode) {
        return "";
    }

    @Override
    public Provider getProvider() {
        return Provider.GITHUB;
    }
}
