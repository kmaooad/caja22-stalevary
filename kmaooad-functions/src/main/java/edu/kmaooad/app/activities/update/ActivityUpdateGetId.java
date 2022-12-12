package edu.kmaooad.app.activities.update;

import edu.kmaooad.app.activities.Utils;
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
public class ActivityUpdateGetId implements Handler {

    private final TelegramService telegramService;

    public ActivityUpdateGetId(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {
            String activityId = callbackQuery.getData();

            ActivityUpdateDto payload = new ActivityUpdateDto();
            payload.setActivityId(activityId);
            stateMachine.updateStateData(callbackQuery.getMessage().getChatId(), ActivityUpdate.GROUP, payload);

            HashMap<String, String> map = new HashMap<>();
            map.put("Title", "title");
            map.put("Description", "description");
            map.put("Date", "date");
            map.put("Time", "time");
            map.put("Location", "location");
            InlineKeyboardMarkup kb = Utils.getKeyboardFromDict(map);

            stateMachine.setState(callbackQuery.getMessage().getChatId(), ActivityUpdate.GET_FIELD);
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Pick a field to edit", kb);
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return ActivityUpdate.GET_ID;
    }
}

