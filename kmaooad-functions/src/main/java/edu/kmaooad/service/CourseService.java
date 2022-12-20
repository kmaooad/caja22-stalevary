package edu.kmaooad.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.dto.CourseUpdateDto;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.model.CourseEntity;
import edu.kmaooad.repository.CourseRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;


    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseEntity> getCourses() {
        return courseRepository.findAll();
    }

    public CourseEntity createCourse(CourseDto courseDto) {
        CourseEntity course = new CourseEntity();

        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());

        return courseRepository.save(course);
    }

    public CourseEntity updateCourse(CourseUpdateDto dto) throws IncorrectIdException, JsonProcessingException {
        CourseEntity activity = courseRepository
                .findById(dto.getCourseId())
                .orElseThrow(IncorrectIdException::new);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(activity);
        JSONObject jsonObject = new JSONObject(json);
        jsonObject.put(dto.getField(), dto.getValue());
        CourseEntity courseUpdated = objectMapper.readValue(jsonObject.toString(), CourseEntity.class);

        return courseRepository.save(courseUpdated);
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }
}
