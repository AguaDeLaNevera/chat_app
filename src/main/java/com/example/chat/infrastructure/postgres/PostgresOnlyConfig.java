package com.example.chat.infrastructure.postgres;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Profile("postgres")
@EnableJpaRepositories(basePackages = "com.example.chat.infrastructure.postgres")
@EntityScan(basePackages = "com.example.chat.infrastructure.postgres")
@SpringBootApplication(
        exclude = {
                MongoAutoConfiguration.class,
                DataMongoAutoConfiguration.class
        }
)
@Configuration
public class PostgresOnlyConfig {
}