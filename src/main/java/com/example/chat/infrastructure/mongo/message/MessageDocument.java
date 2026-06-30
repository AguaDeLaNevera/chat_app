package com.example.chat.infrastructure.mongo.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;


@Document(collection = "messages")
@Getter
@Setter
public class MessageDocument {

    @Id
    private int id;

    @Field("user_id")
    private int userId;

    private String content;

    @Field("sent_at")
    private Instant sentAt;
}