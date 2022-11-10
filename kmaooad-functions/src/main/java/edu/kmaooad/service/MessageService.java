package edu.kmaooad.service;

import edu.kmaooad.dto.BotUpdate;
import edu.kmaooad.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void saveMessage(BotUpdate update) {
        messageRepository.save(update);
    }
}
