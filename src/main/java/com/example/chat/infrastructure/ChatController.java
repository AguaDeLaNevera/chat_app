package com.example.chat.infrastructure;

import com.example.chat.application.MessageUseCase;
import com.example.chat.application.UserUseCase;
import com.example.chat.domain.Message;
import com.example.chat.domain.User;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final MessageUseCase messageUseCase;
    private final UserUseCase userUseCase;

    public ChatController(MessageUseCase messageUseCase, UserUseCase userUseCase) {
        this.messageUseCase = messageUseCase;
        this.userUseCase = userUseCase;
    }

    @GetMapping("/messages")
    public List<Map<String, String>> getMessages() {
        return messageUseCase.getAllMessages().stream().map(msg -> Map.of(
                "id", String.valueOf(msg.id()),
                "userId", String.valueOf(msg.userId()),
                "username", userUseCase.getUsernameWithId(msg.userId()),
                "content", msg.content()
        )).toList();
    }
    @GetMapping("/users")
    public List<User> getUsers() {
        return userUseCase.findAll();
    }
    @GetMapping("/users/{user_id}")
    public User getUserById(@PathVariable int user_id){
        return userUseCase.findUserById(user_id);
    }


    @PostMapping("/message")
    public Message sendMessage(@RequestBody Map<String, String> body) {

        return messageUseCase.sendMessage(
                0,
                Integer.parseInt(body.get("userId")),
                body.get("content"),
                new java.sql.Timestamp(System.currentTimeMillis()));

    }
    @PostMapping("/user")
    public User createUser(@RequestBody Map<String, String> body) {
        return userUseCase.createUser(
                body.get("username")
        );
    }
    @GetMapping("/instance")
    public Map<String, String> instance() throws UnknownHostException {
        return Map.of("instance", InetAddress.getLocalHost().getHostName());
    }
}