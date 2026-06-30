package com.example.chat.infrastructure.mongo.user;

import com.example.chat.domain.User;
import com.example.chat.domain.UserRepository;
import com.example.chat.infrastructure.mongo.SequenceGeneratorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "db.type", havingValue = "mongo")
public class UserMongoRepositoryAdapter implements UserRepository {
    private final UserMongoRepository mongoRepo;
    private final SequenceGeneratorService sequenceGeneratorService;

    public UserMongoRepositoryAdapter(UserMongoRepository mongoRepo, SequenceGeneratorService sequenceGeneratorService) {
        this.mongoRepo = mongoRepo;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @Override
    public User save(User user) {
        UserDocument userDocument = toDocument(user);
        if(userDocument.getId()== 0){
            userDocument.setId(sequenceGeneratorService.generateSequence("users_sequence"));
        }
        UserDocument savedUserDocument = mongoRepo.save(userDocument);
        return toDomain(savedUserDocument);
    }

    @Override
    public Optional<User> findById(int id) {
        return mongoRepo.findById(id).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return mongoRepo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<User> findByUsername(String username) {
        return mongoRepo.findByUsername(username).stream().map(this::toDomain).toList();
    }
    private UserDocument toDocument(User user) {
        UserDocument userDocument = new UserDocument();
        userDocument.setId(user.id());
        userDocument.setUsername(user.username());
        userDocument.setPassword(user.password());
        return userDocument;
    }
    private User toDomain(UserDocument userDocument) {
        return new User(userDocument.getId(), userDocument.getUsername(), userDocument.getPassword());
    }

}
