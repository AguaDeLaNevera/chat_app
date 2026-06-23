package com.example.chat.domain;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(int id);
    List<User> findAll();
    List<User> findByUsername(String username);
}
