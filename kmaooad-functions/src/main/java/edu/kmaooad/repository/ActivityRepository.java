package edu.kmaooad.repository;

import edu.kmaooad.model.ActivityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityRepository extends MongoRepository<ActivityEntity, String> {
    List<ActivityEntity> findByOwnerId(String ownerId);
}
