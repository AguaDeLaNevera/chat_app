package com.example.chat.infrastructure.graphql;

import com.example.chat.application.MessageUseCase;
import com.example.chat.application.UserUseCase;
import com.example.chat.domain.Message;
import com.example.chat.infrastructure.dto.MessageResponse;
import com.example.chat.infrastructure.exceptions.InvalidCredentialsException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Controller
public class MessageResolver {

    private final MessageUseCase messageUseCase;
    private final UserUseCase userUseCase;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageResolver(MessageUseCase messageUseCase,
                           UserUseCase userUseCase,
                           SimpMessagingTemplate messagingTemplate) {
        this.messageUseCase = messageUseCase;
        this.userUseCase = userUseCase;
        this.messagingTemplate = messagingTemplate;
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

        if(authentication == null){
            throw new InvalidCredentialsException();
        }
        int userId = Integer.parseInt(authentication.getName());

        Message message = messageUseCase.sendMessage(
                0,
                userId,
                content,
                Instant.now()
        );

        MessageResponse messageResponse = toMessageResponse(message);

        messagingTemplate.convertAndSend("/topic/messages", messageResponse);

        return message;
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