package com.hanyahunya.auth.adapter.in.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupDto {
    private String email;
    private String password;
    private String locale;
}