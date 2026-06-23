package com.example.chat.domain;

import java.sql.Timestamp;

public record Message(int id, int userId, String content, Timestamp sentAt) {
}
