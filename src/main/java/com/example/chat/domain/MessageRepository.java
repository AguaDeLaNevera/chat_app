package com.example.chat.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(int id);
    List<Message> findAll();
    List<Message> findAllByUserId(int userId);
    List<Message> findAllByContent(String content);
    List<Message> findAllBySentAt(Instant sentAt);
}
