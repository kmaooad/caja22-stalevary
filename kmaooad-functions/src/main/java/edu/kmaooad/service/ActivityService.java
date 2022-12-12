package edu.kmaooad.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.dto.ActivityDto;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.repository.ActivityRepository;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.BasicQuery;
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

    public ActivityEntity updateActivity(ActivityUpdateDto dto) throws IncorrectIdException, JsonProcessingException {
        ActivityEntity activity = activityRepository
                .findById(dto.getActivityId())
                .orElseThrow(IncorrectIdException::new);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(activity);
        JSONObject jsonObject = new JSONObject(json);
        jsonObject.put(dto.getField(), dto.getValue());
        ActivityEntity activityUpdated = objectMapper.readValue(jsonObject.toString(), ActivityEntity.class);

        return activityRepository.save(activityUpdated);
    }

    public void deleteActivity(String id) {
        activityRepository.deleteById(id);
    }

    public List<ActivityEntity> getUserActivities(String userId) {
        return activityRepository.findByOwnerId(userId);
    }
}

