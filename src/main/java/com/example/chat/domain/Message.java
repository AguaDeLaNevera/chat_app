package com.example.chat.domain;


import java.time.Instant;

public record Message(
        int id,
        int userId,
        String content,
        Instant sentAt) {
}
