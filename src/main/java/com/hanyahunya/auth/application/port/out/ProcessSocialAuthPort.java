package com.hanyahunya.auth.application.port.out;

import com.hanyahunya.auth.domain.model.Provider;

public interface ProcessSocialAuthPort {
    void processLogin(String validateCode, String socialSub);

    Provider getProvider();
}
