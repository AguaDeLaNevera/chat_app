package com.example.chat.application;

import com.example.chat.domain.User;
import com.example.chat.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public User createUser(String username, String password){
        String encodedPassword = passwordEncoder.encode(password);
        if(userRepository.findByUsername(username)
                .stream()
                .findFirst()
                .isPresent()
        ){
            throw new RuntimeException("Username already exists");
        }
        User user = new User(0, username,  encodedPassword);
        return userRepository.save(user);
    }
    public User findUserById(int id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public List<User> findAll(){
        return userRepository.findAll();
    }
    public List<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }
    public String getUsernameWithId(int id){
        return findUserById(id).username();
    }
    public User login(String username, String password){
        User user = userRepository.findByUsername(username)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(!passwordEncoder.matches(password, user.password())){
            throw new RuntimeException("Wrong password");
        }
        return user;
    }
}
