package edu.kmaooad.app.competence.update;

import edu.kmaooad.app.activities.update.ActivityUpdate;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.dto.CompetenceUpdateDto;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class CompetencesUpdateValue implements Handler {

    private final TelegramService telegramService;

    private final ActivityService activityService;

    public CompetencesUpdateValue(TelegramService telegramService, ActivityService activityService) {
        this.telegramService = telegramService;
        this.activityService = activityService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            String newCompetence = message.getText();

            CompetenceUpdateDto payload = stateMachine
                    .getStatePayload(message.getChatId(), ActivityUpdate.GROUP, CompetenceUpdateDto.class)
                    .orElseThrow();
            ActivityUpdateDto activity = payload.getActivityUpdateDto();
            activity.setField("competences");

            ActivityEntity entity = payload.getEntity();
            List<String> competences = new java.util.ArrayList<>(entity.getCompetences()
                    .stream()
                    .filter(c -> !c.equals(payload.getCompetence()))
                    .toList());
            competences.add(newCompetence);
            activity.setArray(competences);
            activityService.updateActivity(activity);

            stateMachine.setState(message.getChatId(), new State.Any());
            telegramService.sendMessage(message.getChatId(), "Successfully updated!");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CompetencesUpdate.UPDATE_VALUE;
    }
}

