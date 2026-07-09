package com.example.chat.infrastructure.exceptions;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException() {
        super("message not found");
    }
}
