package edu.kmaooad.app.activities.list;

import edu.kmaooad.app.activities.delete.ActivityDelete;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
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
public class ActivitiesListHandler implements Handler {

    private final TelegramService telegramService;
    private final ActivityService activityService;

    public ActivitiesListHandler(TelegramService telegramService, ActivityService activityService) {
        this.telegramService = telegramService;
        this.activityService = activityService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Your activities:\n");

            List<ActivityEntity> entities = activityService.getUserActivities(message.getFrom().getId().toString());
            for (ActivityEntity entity : entities) {
                sb.append("--------------------\n");
                sb.append("Title: ").append(entity.getTitle()).append("\n");
                sb.append("Description: ").append(entity.getDescription()).append("\n");
                sb.append("Date: ").append(entity.getDate()).append("\n");
                sb.append("Time: ").append(entity.getTime()).append("\n");
                sb.append("Location: ").append(entity.getLocation()).append("\n");
                sb.append("--------------------\n");
            }

            telegramService.sendMessage(message.getChatId(), sb.toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public List<String> getCommands() {
        return List.of("listactivities");
    }
}

