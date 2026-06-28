package com.example.chat.infrastructure.mongo.message;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;


@Document(collection = "messages")
public class MessageDocument {

    @Id
    private int id;
    @Field("user_id")
    private int userId;

    private String content;
    @Field("sent_at")
    private Instant sentAt;

    public int getId() {
        return id;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public void setId(int id) {
        this.id = id;
    }
}