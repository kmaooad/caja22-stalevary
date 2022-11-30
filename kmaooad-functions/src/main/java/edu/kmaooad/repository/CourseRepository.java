package edu.kmaooad.repository;

import edu.kmaooad.model.CourseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<CourseEntity, String> {
}
