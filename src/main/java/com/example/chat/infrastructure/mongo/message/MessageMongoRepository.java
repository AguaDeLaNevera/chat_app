package com.example.chat.infrastructure.mongo.message;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface MessageMongoRepository extends MongoRepository<MessageDocument, Integer> {
    List<MessageDocument> findAllByUserId(int userId);

    List<MessageDocument> findAllByContent(String content);

    List<MessageDocument> findAllBySentAt(Instant sentAt);
}
