package edu.kmaooad.repository;

import edu.kmaooad.model.SessionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<SessionEntity, Long> {
}
