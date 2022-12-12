package edu.kmaooad.app.activities.delete;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class ActivityDeleteHandler implements Handler {

    private final TelegramService telegramService;
    private final ActivityService activityService;

    public ActivityDeleteHandler(TelegramService telegramService, ActivityService activityService) {
        this.telegramService = telegramService;
        this.activityService = activityService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            InlineKeyboardMarkup kb = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            List<ActivityEntity> entities = activityService.getUserActivities(message.getFrom().getId().toString());
            for (ActivityEntity entity : entities) {
                InlineKeyboardButton btn = new InlineKeyboardButton();
                btn.setText(entity.getTitle() + " " + entity.getDate() + " " + entity.getTime());
                btn.setCallbackData(entity.getId());

                rows.add(List.of(btn));
            }

            kb.setKeyboard(rows);

            stateMachine.setState(message.getChatId(), ActivityDelete.GET_ID);
            telegramService.sendMessage(message.getChatId(), "Pick the activity:", kb);
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public List<String> getCommands() {
        return List.of("deleteactivity");
    }
}
