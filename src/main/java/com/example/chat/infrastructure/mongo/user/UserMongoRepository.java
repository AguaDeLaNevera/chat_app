package com.example.chat.infrastructure.mongo.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserMongoRepository extends MongoRepository<UserDocument, Integer> {
    List<UserDocument> findByUsername(String username);
}
