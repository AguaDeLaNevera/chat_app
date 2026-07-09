package com.example.chat.application;

import com.example.chat.domain.Message;
import com.example.chat.domain.MessageRepository;
import com.example.chat.infrastructure.exceptions.MessageNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageUseCaseTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageUseCase messageUseCase;

    @Test
    void sendMessageSavesMessage() {
        Instant sentAt = Instant.parse("2026-07-09T10:00:00Z");
        Message savedMessage = new Message(1, 2, "hello", sentAt);

        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        Message result = messageUseCase.sendMessage(1, 2, "hello", sentAt);

        assertEquals(1, result.id());
        assertEquals(2, result.userId());
        assertEquals("hello", result.content());
        assertEquals(sentAt, result.sentAt());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void findByIdReturnsMessage() {
        Instant sentAt = Instant.parse("2026-07-09T10:00:00Z");
        Message message = new Message(1, 2, "hello", sentAt);
        when(messageRepository.findById(1)).thenReturn(Optional.of(message));

        Message result = messageUseCase.findById(1);

        assertEquals(1, result.id());
        assertEquals("hello", result.content());
        verify(messageRepository).findById(1);
    }

    @Test
    void findByIdThrowsMessageNotFoundException() {
        when(messageRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(
                MessageNotFoundException.class,
                () -> messageUseCase.findById(1)
        );
        verify(messageRepository).findById(1);
    }

    @Test
    void getAllMessagesReturnsListOfMessages() {
        Instant now = Instant.parse("2026-07-09T10:00:00Z");
        List<Message> messages = List.of(
                new Message(1, 2, "hello", now),
                new Message(2, 3, "bye", now)
        );
        when(messageRepository.findAll()).thenReturn(messages);

        List<Message> result = messageUseCase.getAllMessages();

        assertEquals(2, result.size());
        assertEquals("hello", result.get(0).content());
        assertEquals("bye", result.get(1).content());
        verify(messageRepository).findAll();
    }

    @Test
    void findAllByContentReturnsListOfMessages() {
        Instant now = Instant.parse("2026-07-09T10:00:00Z");
        List<Message> messages = List.of(new Message(1, 2, "hello", now));
        when(messageRepository.findAllByContent("hello")).thenReturn(messages);

        List<Message> result = messageUseCase.findAllByContent("hello");

        assertEquals(1, result.size());
        assertEquals("hello", result.get(0).content());
        verify(messageRepository).findAllByContent("hello");
    }

    @Test
    void findAllBySentAtReturnsListOfMessages() {
        Instant sentAt = Instant.parse("2026-07-09T10:00:00Z");
        List<Message> messages = List.of(new Message(1, 2, "hello", sentAt));
        when(messageRepository.findAllBySentAt(sentAt)).thenReturn(messages);

        List<Message> result = messageUseCase.findAllBySentAt(sentAt);

        assertEquals(1, result.size());
        assertEquals(sentAt, result.get(0).sentAt());
        verify(messageRepository).findAllBySentAt(sentAt);
    }
}
