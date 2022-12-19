package edu.kmaooad.app.competence.create;

import edu.kmaooad.app.activities.Utils;
import edu.kmaooad.app.activities.create.ActivityCreate;
import edu.kmaooad.app.activities.update.ActivityUpdate;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;

@Component
public class CompetenceCreateGetActivityId implements Handler {

    private final TelegramService telegramService;

    public CompetenceCreateGetActivityId(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {
            String activityId = callbackQuery.getData();

            ActivityUpdateDto payload = new ActivityUpdateDto();
            payload.setActivityId(activityId);
            payload.setField("competences");
            stateMachine.updateStateData(callbackQuery.getMessage().getChatId(), ActivityUpdate.GROUP, payload);

            stateMachine.setState(callbackQuery.getMessage().getChatId(), CompetenceCreate.GET_COMPETENCES);
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Enter competences. All competences in one message. Each competence in separate line");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CompetenceCreate.GET_ACTIVITY_ID;
    }
}

