package com.example.chat.infrastructure.graphql;

import com.example.chat.application.UserUseCase;
import com.example.chat.domain.User;
import com.example.chat.infrastructure.dto.LoginResponse;
import com.example.chat.infrastructure.dto.UserResponse;
import com.example.chat.infrastructure.keycloak.KeycloakService;
import com.example.chat.infrastructure.security.JwtService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserResolver {

    private final UserUseCase userUseCase;
    private final KeycloakService keycloakService;

    public UserResolver(UserUseCase userUseCase,  KeycloakService keycloakService) {
        this.userUseCase = userUseCase;
        this.keycloakService = keycloakService;

    }

    @QueryMapping
    public List<UserResponse> users() {
        return userUseCase.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    @QueryMapping
    public UserResponse user(@Argument int id) {
        return toUserResponse(userUseCase.findUserById(id));
    }

    @MutationMapping
    public UserResponse register(
            @Argument String username,
            @Argument String password) {

        keycloakService.createUser(username, password);

        User user = userUseCase.createUser(username, password);

        return toUserResponse(user);
    }

//    @MutationMapping
//    public LoginResponse login(
//            @Argument String username,
//            @Argument String password) {
//
//        User user = userUseCase.login(username, password);
//
//        String token = jwtService.generateToken(user);
//
//        return new LoginResponse(token);
//    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.id(),
                user.username()
        );
    }
}