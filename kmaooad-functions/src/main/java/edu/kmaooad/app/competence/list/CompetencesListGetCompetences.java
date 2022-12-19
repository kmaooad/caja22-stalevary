package edu.kmaooad.app.competence.list;

import edu.kmaooad.app.activities.Utils;
import edu.kmaooad.app.activities.create.ActivityCreate;
import edu.kmaooad.app.activities.update.ActivityUpdate;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompetencesListGetCompetences implements Handler {

    private final TelegramService telegramService;
    private final ActivityService activityService;

    public CompetencesListGetCompetences(TelegramService telegramService, ActivityService activityService) {
        this.telegramService = telegramService;
        this.activityService = activityService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {
            String activityId = callbackQuery.getData();
            List<ActivityEntity> entities = activityService.getUserActivities(callbackQuery.getMessage().getChatId().toString());
            entities = entities.stream().filter(a -> a.getId().equals(activityId)).toList();
            ActivityEntity activity = entities.get(0);
            StringBuilder sb = new StringBuilder();
            sb.append("Your competences for activity:\n");
            for (String competence : activity.getCompetences()) {
                sb.append("--------------------\n");
                sb.append("Title: ").append(competence).append("\n");
                sb.append("--------------------\n");
            }
            stateMachine.setState(callbackQuery.getMessage().getChatId(), new State.Any());
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), sb.toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CompetencesList.GET_COMPETENCES;
    }
}

