package com.hanyahunya.auth.application.command;

public record ValidateTfaCommand(String email, String validateCode) {
}
