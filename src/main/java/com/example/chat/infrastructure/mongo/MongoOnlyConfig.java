package com.example.chat.infrastructure.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ConditionalOnProperty(name = "db.type", havingValue = "mongo")
@EnableMongoRepositories(basePackages = "com.example.chat.infrastructure.mongo")
public class MongoOnlyConfig {
}