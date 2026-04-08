package com.example.authodo.application.auth.dto.command;

public record SignupCommand(
    String email,
    String password,
    String nickname
) {

}
