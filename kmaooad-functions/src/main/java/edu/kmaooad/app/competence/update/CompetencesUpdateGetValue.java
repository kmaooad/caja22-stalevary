package edu.kmaooad.app.competence.update;

import edu.kmaooad.app.activities.update.ActivityUpdate;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.CompetenceUpdateDto;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CompetencesUpdateGetValue implements Handler {

    private final TelegramService telegramService;
    private final ActivityService activityService;

    public CompetencesUpdateGetValue(TelegramService telegramService, ActivityService activityService) {
        this.activityService = activityService;
        this.telegramService = telegramService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {
            String competence = callbackQuery.getData();
            CompetenceUpdateDto payload = stateMachine
                    .getStatePayload(callbackQuery.getMessage().getChatId(), ActivityUpdate.GROUP, CompetenceUpdateDto.class)
                    .orElseThrow();
            payload.setCompetence(competence);

            stateMachine.updateStateData(callbackQuery.getMessage().getChatId(), ActivityUpdate.GROUP, payload);
            stateMachine.setState(callbackQuery.getMessage().getChatId(), CompetencesUpdate.UPDATE_VALUE);
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Type new competence name");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CompetencesUpdate.GET_VALUE;
    }
}

