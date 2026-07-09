package com.example.chat.application;

import com.example.chat.domain.User;
import com.example.chat.domain.UserRepository;
import com.example.chat.infrastructure.exceptions.InvalidCredentialsException;
import com.example.chat.infrastructure.exceptions.UserAlreadyExistsException;
import com.example.chat.infrastructure.exceptions.UserNotFoundException;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

@Service
public class UserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String username, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        if (userRepository.findByUsername(username)
                .isPresent()
        ) {
            throw new UserAlreadyExistsException();
        }
        User user = new User(0, username, encodedPassword);
        return userRepository.save(user);
    }

    public User findUserById(int id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .stream()
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

    public String getUsernameWithId(int id) {
        return findUserById(id).username();
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .stream()
                .findFirst()
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(password, user.password())) {
            throw new InvalidCredentialsException();
        }
        return user;
    }
}
