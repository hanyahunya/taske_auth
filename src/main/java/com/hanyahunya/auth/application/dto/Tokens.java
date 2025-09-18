package com.hanyahunya.auth.application.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Tokens {
    private String accessToken;
    private String refreshToken;
}
