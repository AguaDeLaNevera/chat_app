package com.example.chat.infrastructure.mongo.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<UserDocument, Integer> {
    Optional<UserDocument> findByUsername(String username);
}
