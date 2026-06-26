package com.example.chat.infrastructure.dto;

public record CreateMessageRequest(
        int userId,
        String content
) {
}
