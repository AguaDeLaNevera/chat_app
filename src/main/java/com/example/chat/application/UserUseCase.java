package com.example.chat.application;

import com.example.chat.domain.User;
import com.example.chat.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class UserUseCase {
    private final UserRepository userRepository;
    public UserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User createUser(String username){
        User user = new User(0, username);
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

}
