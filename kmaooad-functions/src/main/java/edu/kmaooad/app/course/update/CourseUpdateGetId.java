package edu.kmaooad.app.course.update;


import edu.kmaooad.app.activities.Utils;
import edu.kmaooad.app.activities.update.ActivityUpdate;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.dto.CourseUpdateDto;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;

@Component
public class CourseUpdateGetId implements Handler {

    private final TelegramService telegramService;

    public CourseUpdateGetId(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {
            String courseId = callbackQuery.getData();

            CourseUpdateDto payload = new CourseUpdateDto();
            payload.setCourseId(courseId);
            stateMachine.updateStateData(callbackQuery.getMessage().getChatId(), CourseUpdate.GROUP, payload);

            HashMap<String, String> map = new HashMap<>();
            map.put("Title", "title");
            map.put("Description", "description");
            InlineKeyboardMarkup kb = Utils.getKeyboardFromDict(map);

            stateMachine.setState(callbackQuery.getMessage().getChatId(), CourseUpdate.GET_FIELD);
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Pick a field to edit", kb);
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CourseUpdate.GET_ID;
    }
}

