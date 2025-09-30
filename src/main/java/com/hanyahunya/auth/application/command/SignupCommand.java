package com.hanyahunya.auth.application.command;

public record SignupCommand (
        String email,
        String password,
        String locale
) {}