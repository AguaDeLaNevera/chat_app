package com.example.chat.infrastructure.postgres.user;

import com.example.chat.domain.User;
import com.example.chat.domain.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name="db.type", havingValue = "postgres",  matchIfMissing = true)
public class UserJpaRepositoryAdapter implements UserRepository {
    private final UserJpaRepository jpaRepo;

    public UserJpaRepositoryAdapter(UserJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = toEntity(user);
        UserEntity savedUserEntity = jpaRepo.save(userEntity);
        return toDomain(savedUserEntity);
    }

    @Override
    public Optional<User> findById(int id) {
        return jpaRepo.findById(id).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<User> findByUsername(String username) {
        return jpaRepo.findByUsername(username).stream().map(this::toDomain).toList();
    }
    private UserEntity toEntity(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.id());
        userEntity.setUsername(user.username());
        return userEntity;
    }
    private User toDomain(UserEntity userEntity) {
        return new User(userEntity.getId(), userEntity.getUsername());
    }

}
