package com.example.chat.infrastructure.postgres;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(name = "db.type", havingValue = "postgres")
@EnableJpaRepositories(basePackages = "com.example.chat.infrastructure.postgres")
@EntityScan(basePackages = "com.example.chat.infrastructure.postgres")
public class PostgresOnlyConfig {
}