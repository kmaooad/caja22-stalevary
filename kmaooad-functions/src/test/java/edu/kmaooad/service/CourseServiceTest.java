package edu.kmaooad.service;

import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.dto.CourseProjectDto;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.CourseEntity;
import edu.kmaooad.model.CourseProjectEntity;
import edu.kmaooad.repository.CourseRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CourseServiceTest {

    private final CourseRepository courseRepository = mock(CourseRepository.class);
    private final CourseService courseService = new CourseService(courseRepository);

    @Test
    void shouldCreateCourse() {
        when(courseRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        CourseDto course = new CourseDto("title", "description");
        CourseEntity result = courseService.createCourse(course);

        verify(courseRepository, times(1)).save(any());
        assertEquals(result.getTitle(), course.getTitle());
        assertEquals(result.getDescription(), course.getDescription());
    }

    @Test
    void shouldDeleteCourse() {
        courseService.deleteCourse("if");
        verify(courseRepository, times(1)).deleteById(any());
    }

    @Test
    void shouldUpdateCourse() throws IncorrectIdException {
        when(courseRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        when(courseRepository.findById(any()))
                .then(invocationOnMock ->
                        Optional.of(new CourseEntity(invocationOnMock.getArgument(0), "old_title", "old_description", new ArrayList<>()))
                );

        CourseDto course = new CourseDto("title", "description");
        CourseEntity result = courseService.updateCourse("1", course);

        verify(courseRepository, times(1)).save(any());
        verify(courseRepository, times(1)).findById(any());

        assertEquals(result.getTitle(), course.getTitle());
        assertEquals(result.getDescription(), course.getDescription());
    }

    @Test
    void shouldThrowIncorrectIdExceptionOnUpdate() {
        when(courseRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        when(courseRepository.findById(any()))
                .thenReturn(Optional.empty());

        CourseDto course = new CourseDto("title", "description");
        assertThrows(IncorrectIdException.class, () -> courseService.updateCourse("1", course));

        verify(courseRepository, times(1)).findById(any());
        verify(courseRepository, times(0)).save(any());
    }

    @Test
    void shouldReturnAllCourses() {
        List<CourseEntity> courses = new ArrayList<>();
        courses.add(new CourseEntity("1", "title", "description", new ArrayList<>()));
        courses.add(new CourseEntity("2", "title", "description", new ArrayList<>()));
        courses.add(new CourseEntity("3", "title", "description", new ArrayList<>()));

        when(courseRepository.findAll())
                .thenReturn(courses);

        List<CourseEntity> resultCourses = courseService.getCourses();

        assertIterableEquals(resultCourses, courses);
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowIncorrectIdExceptionOnAddCourseProjects() {
        when(courseRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(IncorrectIdException.class, () -> courseService.addCourseProjects("1", new ArrayList<>()));

        verify(courseRepository, times(1)).findById(any());
        verify(courseRepository, times(0)).save(any());
    }

    @Test
    void shouldAddCourseProjects() throws IncorrectIdException {
        List<CourseProjectDto> courseProjectDtoList = new ArrayList<>();
        courseProjectDtoList.add(new CourseProjectDto("1", "2", "3", "12:20"));
        courseProjectDtoList.add(new CourseProjectDto("1", "2", "3", "12:20"));
        courseProjectDtoList.add(new CourseProjectDto("1", "2", "3", "12:20"));

        CourseEntity courseEntity = new CourseEntity("1", "title", "description", new ArrayList<>());

        when(courseRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        when(courseRepository.findById(any()))
                .thenReturn(Optional.of(courseEntity));

        CourseEntity resultCourse = courseService.addCourseProjects("1", courseProjectDtoList);

        IntStream.range(0, courseProjectDtoList.size())
                .forEach(i -> {
                    CourseProjectEntity entity = resultCourse.getProjects().get(i);
                    CourseProjectDto dto = courseProjectDtoList.get(i);

                    assertTrue(entity.getTitle().equals(dto.getTitle())
                            && entity.getDescription().equals(dto.getDescription())
                            && entity.getRequirements().equals(dto.getRequirements())
                            && entity.getTime().equals(dto.getTime())
                    );
                });

        assertEquals(resultCourse.getProjects().size(), courseProjectDtoList.size());
        verify(courseRepository, times(1)).findById(any());
    }

    @Test
    void shouldGetCourseById() throws IncorrectIdException {
        when(courseRepository.findById(any()))
                .thenReturn(Optional.of(new CourseEntity("id", "title", "desc", new ArrayList<>())));

        courseService.getCourse("id");

        verify(courseRepository, times(1)).findById(any());
    }

    @Test
    void shouldThrowExceptionOnIncorrectId() {
        when(courseRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(IncorrectIdException.class, () -> courseService.getCourse("id"));

        verify(courseRepository, times(1)).findById(any());
    }
}
