package com.hanyahunya.auth.application.command;

import com.hanyahunya.auth.domain.model.Provider;

public record SocialLoginCommand(Provider provider, String validateCode, String locale) {
}
