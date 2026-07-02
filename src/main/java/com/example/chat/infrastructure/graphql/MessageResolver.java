package com.example.chat.infrastructure.graphql;

import com.example.chat.application.MessageUseCase;
import com.example.chat.application.UserUseCase;
import com.example.chat.domain.Message;
import com.example.chat.infrastructure.dto.MessageResponse;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Controller
public class MessageResolver {

    private final MessageUseCase messageUseCase;
    private final UserUseCase userUseCase;

    public MessageResolver(MessageUseCase messageUseCase,
                           UserUseCase userUseCase) {
        this.messageUseCase = messageUseCase;
        this.userUseCase = userUseCase;
    }

    @QueryMapping
    public List<MessageResponse> messages() {
        return messageUseCase.getAllMessages()
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @MutationMapping
    public Message sendMessage(
            @Argument String content,
            Authentication authentication) {

        int userId = Integer.parseInt(authentication.getName());

        return messageUseCase.sendMessage(
                0,
                userId,
                content,
                Instant.now()
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