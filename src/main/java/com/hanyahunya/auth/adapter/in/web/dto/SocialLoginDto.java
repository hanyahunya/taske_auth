package com.hanyahunya.auth.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanyahunya.auth.global.validation.SupportedLocale;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SocialLoginDto {
    @NotBlank
    @JsonProperty("code")
    private String validateCode;
    @NotBlank
    private String idToken;
    @NotBlank
    private String nonce;
    @NotBlank
    @SupportedLocale
    private String locale;
}
