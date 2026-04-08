package com.example.authodo.application.auth.dto.command;

public record LoginCommand(
    String email,
    String password
) {

}
