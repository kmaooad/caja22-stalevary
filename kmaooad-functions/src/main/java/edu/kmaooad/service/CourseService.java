package edu.kmaooad.service;

import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.dto.CourseProjectDto;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.CourseEntity;
import edu.kmaooad.model.CourseProjectEntity;
import edu.kmaooad.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CourseService {

    private final CourseRepository courseRepository;


    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseEntity> getCourses() {
        return courseRepository.findAll();
    }

    public CourseEntity getCourse(String id) throws IncorrectIdException {
        return courseRepository
                .findById(id)
                .orElseThrow(IncorrectIdException::new);
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
        course.setDescription(courseDto.getDescription());

        return courseRepository.save(course);
    }

    public CourseEntity addCourseProjects(String id, List<CourseProjectDto> courseProjectDtoList) throws IncorrectIdException {
        CourseEntity course = courseRepository
                .findById(id)
                .orElseThrow(IncorrectIdException::new);

        List<CourseProjectEntity> courseProjectEntityList =
                Stream.concat(courseProjectDtoList.stream()
                                .map(dto -> {
                                    CourseProjectEntity entity = new CourseProjectEntity();
                                    entity.setTitle(dto.getTitle());
                                    entity.setDescription(dto.getDescription());
                                    entity.setRequirements(dto.getRequirements());
                                    entity.setTime(dto.getTime());
                                    return entity;
                                }),
                        course.getProjects().stream()
                ).toList();

        course.setProjects(courseProjectEntityList);
        return courseRepository.save(course);
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }
}
