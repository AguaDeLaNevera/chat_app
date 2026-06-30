package com.example.chat.infrastructure.mongo.user;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Setter
public class UserDocument {

    @Id
    private int id;

    private String username;

    private String password;
}
