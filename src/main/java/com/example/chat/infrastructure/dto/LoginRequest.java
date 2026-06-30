package com.example.chat.infrastructure.dto;

public record LoginRequest(
        String username,
        String password
) {
}
