package edu.kmaooad.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kmaooad.dto.ActivityDto;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.model.CourseEntity;
import edu.kmaooad.repository.ActivityRepository;
import edu.kmaooad.repository.CourseRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ActivityServiceTest {

    private final ActivityRepository activityRepository = mock(ActivityRepository.class);
    private final ActivityService activityService = new ActivityService(activityRepository);

    @Test
    void shouldCreateActivity() {
        when(activityRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        ActivityDto activityDto = new ActivityDto("owner_id", "title", "description", "11.11.2222", "11:11", "foo");
        activityService.createActivity(activityDto);

        verify(activityRepository, times(1)).save(any());
    }

    @Test
    void shouldDeleteCourse() {
        activityService.deleteActivity("id");
        verify(activityRepository, times(1)).deleteById(any());
    }

    @Test
    void shouldUpdateCourse() throws IncorrectIdException, JsonProcessingException {
        when(activityRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        when(activityRepository.findById(any()))
                .then(invocationOnMock ->
                        Optional.of(new ActivityEntity(invocationOnMock.getArgument(0), "owner_id", "old_title", "old_description", "11.11.2222", "11:11", "foo", new ArrayList<String>()))
                );

        ActivityUpdateDto activityUpdateDto = new ActivityUpdateDto("id", "title", "new_title", null);
        ActivityEntity result = activityService.updateActivity(activityUpdateDto);

        verify(activityRepository, times(1)).save(any());
        verify(activityRepository, times(1)).findById(any());

        assertEquals(result.getTitle(), activityUpdateDto.getValue());
    }

    @Test
    void shouldThrowIncorrectIdExceptionOnUpdate() {
        when(activityRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        when(activityRepository.findById(any()))
                .thenReturn(Optional.empty());

        ActivityUpdateDto dto = new ActivityUpdateDto("id", "field", "description", null);
        assertThrows(IncorrectIdException.class, () -> activityService.updateActivity(dto));

        verify(activityRepository, times(1)).findById(any());
        verify(activityRepository, times(0)).save(any());
    }

    @Test
    void shouldReturnAllCourses() {
        List<ActivityEntity> activities = new ArrayList<>();
        activities.add(new ActivityEntity("1", "11", "title1", "description1", "11.11.11", "11:11", "foo", new ArrayList<String>()));
        activities.add(new ActivityEntity("2", "22", "title2", "description2", "22.22.22", "22:22", "bar", new ArrayList<String>()));
        activities.add(new ActivityEntity("3", "33", "title3", "description3", "22.22.22", "22:22", "baz", new ArrayList<String>()));

        when(activityRepository.findAll())
                .thenReturn(activities);

        List<ActivityEntity> resultCourses = activityService.getActivities();

        assertIterableEquals(resultCourses, activities);
        verify(activityRepository, times(1)).findAll();
    }
}
