package edu.kmaooad.app.competence.delete;

import edu.kmaooad.app.activities.Utils;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
public class CompetenceDeleteHandler implements Handler {

    private final TelegramService telegramService;
    private final ActivityService activityService;

    public CompetenceDeleteHandler(TelegramService telegramService, ActivityService activityService) {
        this.telegramService = telegramService;
        this.activityService = activityService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            List<ActivityEntity> entities = activityService.getUserActivities(message.getFrom().getId().toString());
            InlineKeyboardMarkup kb = Utils.getActivitiesKeyboard(entities);

            stateMachine.setState(message.getChatId(), CompetenceDelete.GET_COMPETENCES);
            telegramService.sendMessage(message.getChatId(), "Pick the activity:", kb);
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public List<String> getCommands() {
        return List.of("deletecompetence");
    }
}
