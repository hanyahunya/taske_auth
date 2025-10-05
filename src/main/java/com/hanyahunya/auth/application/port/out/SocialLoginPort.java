package com.hanyahunya.auth.application.port.out;

import com.hanyahunya.auth.domain.model.Provider;

public interface SocialLoginPort {
    String processLogin(String validateCode);

    Provider getProvider();
}
