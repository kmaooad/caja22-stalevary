package edu.kmaooad.repository;

import edu.kmaooad.dto.BotUpdate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<BotUpdate, String> {
}
