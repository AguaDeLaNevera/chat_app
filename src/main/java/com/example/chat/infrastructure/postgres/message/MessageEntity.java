package com.example.chat.infrastructure.postgres.message;

import com.example.chat.infrastructure.postgres.user.UserEntity;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name="messages")
public class MessageEntity {
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity user;
    @Column(name="content")
    private String content;
    @Column(name="sent_at")
    private Timestamp sentAt;

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setUser(UserEntity user) {
        this.user = user;
    }
    public UserEntity getUser() {
        return user;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }
    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
    public Timestamp getSentAt() {
        return sentAt;
    }

}
