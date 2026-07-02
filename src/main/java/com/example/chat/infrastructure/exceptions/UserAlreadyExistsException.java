package com.example.chat.infrastructure.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("Username already exists");
    }
}
