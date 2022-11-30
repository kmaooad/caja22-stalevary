package edu.kmaooad.service;

import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.CourseEntity;
import edu.kmaooad.repository.CourseRepository;
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

    public CourseEntity updateCourse(String id, CourseDto courseDto) throws IncorrectIdException {
        CourseEntity course = courseRepository
                .findById(id)
                .orElseThrow(IncorrectIdException::new);

        course.setTitle(courseDto.getTitle());
        course.setDescription(course.getDescription());

        return courseRepository.save(course);
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }
}
