package com.example.chat.infrastructure;

import com.example.chat.application.JwtService;
import com.example.chat.application.MessageUseCase;
import com.example.chat.application.UserUseCase;
import com.example.chat.domain.Message;
import com.example.chat.domain.User;
import com.example.chat.infrastructure.dto.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final MessageUseCase messageUseCase;
    private final UserUseCase userUseCase;
    private final JwtService jwtService;

    public ChatController(MessageUseCase messageUseCase, UserUseCase userUseCase, JwtService jwtService) {
        this.messageUseCase = messageUseCase;
        this.userUseCase = userUseCase;
        this.jwtService = jwtService;
    }
    // API "GET" ENDPOINTS
    @GetMapping("/messages")
    public List<MessageResponse> getMessages() {
        return messageUseCase.getAllMessages().stream().
                map(this::toMessageResponse).toList();
    }
    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return userUseCase.findAll().stream().map(this::toUserResponse
        ).toList();
    }
    @GetMapping("/users/{user_id}")
    public UserResponse getUserById(@PathVariable int user_id){

        User user = userUseCase.findUserById(user_id);
        return toUserResponse(user);
    }
    // API DIFF INSTANCE CHECKER ENDPOINT
    @GetMapping("/instance")
    public Map<String, String> instance() throws UnknownHostException {
        return Map.of("instance", InetAddress.getLocalHost().getHostName());
    }
    // API "POST" ENDPOINTS
    @PostMapping("/message")
    public Message sendMessage(@RequestBody CreateMessageRequest request, Authentication authentication) {
        int userId = Integer.parseInt(authentication.getName());
        return messageUseCase.sendMessage(
                0,
                userId,
                request.content(),
                Instant.now()
        );
    }
    @PostMapping("/register")
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        User user = userUseCase.createUser(
                request.username(),
                request.password()
        );
        return toUserResponse(user);
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        User user = userUseCase.login(
                request.username(),
                request.password()
        );
        String token = jwtService.generateToken(user);
        return new LoginResponse(token);
    }
    // DTO CONVERTERS
    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.id(),
                user.username()
        );
    }
    private MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.id(),
                message.userId(),
                userUseCase.getUsernameWithId(message.userId()),
                message.content()
        );
    }
}