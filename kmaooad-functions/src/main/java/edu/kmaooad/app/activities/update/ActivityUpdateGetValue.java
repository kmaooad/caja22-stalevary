package edu.kmaooad.app.activities.update;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class ActivityUpdateGetValue implements Handler {

    private final TelegramService telegramService;

    private final ActivityService activityService;

    public ActivityUpdateGetValue(TelegramService telegramService, ActivityService activityService) {
        this.telegramService = telegramService;
        this.activityService = activityService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            ActivityUpdateDto payload = stateMachine
                    .getStatePayload(message.getChatId(), ActivityUpdate.GROUP, ActivityUpdateDto.class)
                    .orElseThrow();

            payload.setValue(message.getText());
            activityService.updateActivity(payload);

            stateMachine.setState(message.getChatId(), new State.Any());
            telegramService.sendMessage(message.getChatId(), "Successfully updated!");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return ActivityUpdate.GET_VALUE;
    }
}

