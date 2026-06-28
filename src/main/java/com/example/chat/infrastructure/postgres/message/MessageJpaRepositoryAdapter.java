package com.example.chat.infrastructure.postgres.message;

import com.example.chat.domain.Message;
import com.example.chat.domain.MessageRepository;
import com.example.chat.infrastructure.postgres.user.UserEntity;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class MessageJpaRepositoryAdapter implements MessageRepository {
    private final MessageJpaRepository jpaRepo;

    public MessageJpaRepositoryAdapter(MessageJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public Message save(Message message) {
        MessageEntity messageEntity = toEntity(message);
        MessageEntity saved = jpaRepo.save(messageEntity);
        return toDomain(saved);
    }

    @Override
    public Optional<Message> findById(int id) {
        return jpaRepo.findById(id).map(this::toDomain);
    }

    @Override
    public List<Message> findAll() {
        return jpaRepo.findAllWithUser()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Message> findAllByUserId(int userId) {
        return jpaRepo.findAllByUser_Id(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Message> findAllByContent(String content) {
        return jpaRepo.findAllByContent(content).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Message> findAllBySentAt(Timestamp sentAt) {
        return jpaRepo.findAllBySentAt(sentAt).stream().map(this::toDomain).toList();
    }
    private MessageEntity toEntity(Message message){
        MessageEntity entity = new MessageEntity();

        entity.setId(message.id());

        UserEntity user = new UserEntity();
        user.setId(message.userId());
        entity.setUser(user);

        entity.setContent(message.content());
        entity.setSentAt(message.sentAt());

        return entity;
    }
    private Message toDomain(MessageEntity entity){
        return new Message(
                entity.getId(),
                entity.getUser().getId(),
                entity.getContent(),
                entity.getSentAt()
        );
    }
}
