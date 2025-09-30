package com.hanyahunya.auth.adapter.in.web.dto;

import com.hanyahunya.auth.global.validation.SupportedLocale;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    @SupportedLocale
    private String locale;
}