package com.example.chat.application;

import com.example.chat.domain.Message;
import com.example.chat.domain.MessageRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class MessageUseCase {
    private final MessageRepository messageRepository;

    public MessageUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    public Message sendMessage(int id, int userId, String content, Timestamp sentAt) {
        Message message = new Message(id, userId, content, sentAt);
        return messageRepository.save(message);
    }
    public Message findById(int id) {
        return messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));
    }
    public List<Message> getAllMessages(){
        return messageRepository.findAll();
    }

    public List<Message> findAllByContent(String content){
        return messageRepository.findAllByContent(content);
    }
    public List<Message> findAllBySentAt(Timestamp sentAt){
        return messageRepository.findAllBySentAt(sentAt);
    }
}
