package com.example.chat.infrastructure.dto;

public record MessageResponse(
        int id,
        int userId,
        String username,
        String content
){
}
