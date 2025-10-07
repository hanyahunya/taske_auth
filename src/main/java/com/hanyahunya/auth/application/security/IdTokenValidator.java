package com.hanyahunya.auth.application.security;

import com.hanyahunya.auth.domain.model.Provider;

public interface IdTokenValidator {
    String validateAndGetSub(String idToken, String nonce);
    Provider getProvider();
}
