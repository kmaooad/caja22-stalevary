package edu.kmaooad.app.activities.create;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityDto;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class ActivityCreateGetLocation implements Handler {

    private final TelegramService telegramService;
    private final ActivityService activityService;

    public ActivityCreateGetLocation(TelegramService telegramService, ActivityService activityService) {
        this.telegramService = telegramService;
        this.activityService = activityService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            ActivityDto payload = stateMachine
                    .getStatePayload(message.getChatId(), ActivityCreate.GROUP, ActivityDto.class)
                    .orElseThrow();

            payload.setLocation(message.getText());
            activityService.createActivity(payload);
            stateMachine.setState(message.getChatId(), new State.Any());
            telegramService.sendMessage(message.getChatId(), "Activity successfully saved");
        } catch (Exception exception) {
            telegramService.sendMessage(message.getChatId(), "Activity data wasn't saved. Error occurred");
        }
    }

    @Override
    public State getState() {
        return ActivityCreate.GET_LOCATION;
    }
}
