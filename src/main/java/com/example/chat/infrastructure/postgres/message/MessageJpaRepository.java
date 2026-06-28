package com.example.chat.infrastructure.postgres.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Integer> {
    Optional<MessageEntity> findById(int id);
    @Query("""
    SELECT m FROM MessageEntity m
    JOIN FETCH m.user
    """)
    List<MessageEntity> findAllWithUser();
    List<MessageEntity> findAllByUser_Id(int userId);
    List<MessageEntity> findAllByContent(String content);
    List<MessageEntity> findAllBySentAt(Timestamp sentAt);
}
