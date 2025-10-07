package com.hanyahunya.auth.application.port.out;

import com.hanyahunya.auth.domain.model.Provider;

public interface SocialLoginPort {
    void processLogin(String validateCode);

    Provider getProvider();
}
