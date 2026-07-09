package com.example.chat.infrastructure.postgres.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findById(int id);
    List<UserEntity> findAll();
    Optional<UserEntity> findByUsername(String username);
}
