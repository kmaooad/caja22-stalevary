package edu.kmaooad.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.dto.CourseUpdateDto;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.CourseEntity;
import edu.kmaooad.repository.CourseRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    void shouldUpdateCourse() throws IncorrectIdException, JsonProcessingException {
        when(courseRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        when(courseRepository.findById(any()))
                .then(invocationOnMock ->
                        Optional.of(new CourseEntity(invocationOnMock.getArgument(0), "old_title", "old_description"))
                );

        CourseUpdateDto courseUpdateDto = new CourseUpdateDto("1","title","new_title");
        CourseEntity result = courseService.updateCourse( courseUpdateDto);

        verify(courseRepository, times(1)).save(any());
        verify(courseRepository, times(1)).findById(any());

        assertEquals(result.getTitle(), courseUpdateDto.getValue());
    }

    @Test
    void shouldThrowIncorrectIdExceptionOnUpdate() {
        when(courseRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        when(courseRepository.findById(any()))
                .thenReturn(Optional.empty());

        CourseUpdateDto courseUpdateDto = new CourseUpdateDto("1","title","new_title");

        assertThrows(IncorrectIdException.class, () -> courseService.updateCourse(courseUpdateDto));

        verify(courseRepository, times(1)).findById(any());
        verify(courseRepository, times(0)).save(any());
    }

    @Test
    void shouldReturnAllCourses() {
        List<CourseEntity> courses = new ArrayList<>();
        courses.add(new CourseEntity("1", "title", "description"));
        courses.add(new CourseEntity("2", "title", "description"));
        courses.add(new CourseEntity("3", "title", "description"));

        when(courseRepository.findAll())
                .thenReturn(courses);

        List<CourseEntity> resultCourses = courseService.getCourses();

        assertIterableEquals(resultCourses, courses);
        verify(courseRepository, times(1)).findAll();
    }
}
