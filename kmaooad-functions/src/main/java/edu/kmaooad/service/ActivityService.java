package edu.kmaooad.service;

import edu.kmaooad.dto.ActivityDto;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.repository.ActivityRepository;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;


    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<ActivityEntity> getActivities() {
        return activityRepository.findAll();
    }

    public void createActivity(ActivityDto dto) {
        ActivityEntity activity = new ActivityEntity();
        activity.setOwnerId(dto.getOwnerId());
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setDate(dto.getDate());
        activity.setTime(dto.getTime());
        activity.setLocation(dto.getLocation());


        activityRepository.save(activity);
    }

    public ActivityEntity updateActivity(String id, ActivityDto dto) throws IncorrectIdException {
        ActivityEntity activity = activityRepository
                .findById(id)
                .orElseThrow(IncorrectIdException::new);

        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setDate(dto.getDate());
        activity.setTime(dto.getTime());
        activity.setLocation(dto.getLocation());

        return activityRepository.save(activity);
    }

    public void deleteActivity(String id) {
        activityRepository.deleteById(id);
    }

    public List<ActivityEntity> getUserActivities(String userId) {
        return activityRepository.findByOwnerId(userId);
    }
}

