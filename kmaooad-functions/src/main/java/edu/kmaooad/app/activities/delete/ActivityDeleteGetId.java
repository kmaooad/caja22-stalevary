package edu.kmaooad.app.activities.delete;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class ActivityDeleteGetId implements Handler {

    private final TelegramService telegramService;
    private final ActivityService activityService;

    public ActivityDeleteGetId(TelegramService telegramService, ActivityService activityService) {
        this.telegramService = telegramService;
        this.activityService = activityService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {
            String activityId = callbackQuery.getData();
            activityService.deleteActivity(activityId);

            stateMachine.setState(callbackQuery.getMessage().getChatId(), new State.Any());
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Activity was successfully deleted!");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return ActivityDelete.GET_ID;
    }
}
