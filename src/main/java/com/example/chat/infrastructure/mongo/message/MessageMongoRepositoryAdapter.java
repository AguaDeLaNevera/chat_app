package com.example.chat.infrastructure.mongo.message;

import com.example.chat.domain.Message;
import com.example.chat.domain.MessageRepository;
import com.example.chat.infrastructure.mongo.SequenceGeneratorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "db.type", havingValue = "mongo")
public class MessageMongoRepositoryAdapter implements MessageRepository {

    private final MessageMongoRepository mongoRepo;
    private final SequenceGeneratorService sequenceGeneratorService;

    public MessageMongoRepositoryAdapter(MessageMongoRepository mongoRepo, SequenceGeneratorService sequenceGeneratorService) {
        this.mongoRepo = mongoRepo;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @Override
    public Message save(Message message) {
        MessageDocument messageDocument = toDocument(message);
        if (messageDocument.getId() == 0){
            messageDocument.setId(sequenceGeneratorService.generateSequence("messages_sequence"));
        }
        MessageDocument saved = mongoRepo.save(messageDocument);
        return toDomain(saved);
    }

    @Override
    public Optional<Message> findById(int id) {
        return mongoRepo.findById(id).map(this::toDomain);
    }

    @Override
    public List<Message> findAll() {
        return mongoRepo.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Message> findAllByUserId(int userId) {
        return mongoRepo.findAllByUserId(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Message> findAllByContent(String content) {
        return mongoRepo.findAllByContent(content)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Message> findAllBySentAt(Instant sentAt) {
        return mongoRepo.findAllBySentAt(sentAt)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private MessageDocument toDocument(Message message) {
        MessageDocument doc = new MessageDocument();;
        doc.setId(message.id());
        doc.setUserId(message.userId());
        doc.setContent(message.content());
        doc.setSentAt(message.sentAt());

        return doc;
    }

    private Message toDomain(MessageDocument doc) {
        return new Message(
                doc.getId(),
                doc.getUserId(),
                doc.getContent(),
                doc.getSentAt()
        );
    }
}