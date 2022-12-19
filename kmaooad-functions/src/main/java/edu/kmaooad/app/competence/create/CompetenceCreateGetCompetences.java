package edu.kmaooad.app.competence.create;

import edu.kmaooad.app.activities.create.ActivityCreate;
import edu.kmaooad.app.activities.update.ActivityUpdate;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityDto;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.List;

@Component
public class CompetenceCreateGetCompetences implements Handler {

    private final TelegramService telegramService;
    private final ActivityService activityService;

    public CompetenceCreateGetCompetences(TelegramService telegramService, ActivityService activityService) {
        this.telegramService = telegramService;
        this.activityService = activityService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            ActivityUpdateDto payload = stateMachine
                    .getStatePayload(message.getChatId(), ActivityUpdate.GROUP, ActivityUpdateDto.class)
                    .orElseThrow();
            List<String> competences = Arrays.asList(message.getText().split("\\n"));
            payload.setArray(competences);
            activityService.updateActivity(payload);

            stateMachine.setState(message.getChatId(), new State.Any());
            telegramService.sendMessage(message.getChatId(), "Successfully created!");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CompetenceCreate.GET_COMPETENCES;
    }
}
