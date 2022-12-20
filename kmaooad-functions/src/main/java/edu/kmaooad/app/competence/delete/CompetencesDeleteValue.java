package edu.kmaooad.app.competence.delete;

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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
public class CompetencesDeleteValue implements Handler {

    private final TelegramService telegramService;
    private final ActivityService activityService;

    public CompetencesDeleteValue(TelegramService telegramService, ActivityService activityService) {
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

            ActivityUpdateDto activityUpdateDto = payload.getActivityUpdateDto();
            activityUpdateDto.setField("competences");

            ActivityEntity activity = payload.getEntity();
            List<String> competences = activity.getCompetences().stream()
                            .filter(c -> !c.equals(competence)).toList();
            activityUpdateDto.setArray(competences);
            activityService.updateActivity(activityUpdateDto);

            stateMachine.setState(callbackQuery.getMessage().getChatId(), new State.Any());
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Competence is deleted!");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CompetencesDelete.DELETE;
    }
}

