package com.hanyahunya.auth.application.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupDto {
    private String email;
    private String password;
    private String locale;
}