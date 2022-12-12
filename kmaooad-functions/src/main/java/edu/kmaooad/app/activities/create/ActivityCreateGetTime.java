package edu.kmaooad.app.activities.create;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityDto;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class ActivityCreateGetTime implements Handler {
    private final TelegramService telegramService;

    public ActivityCreateGetTime(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            if (!message.getText().matches("\\d{2}:\\d{2}")) {
                telegramService.sendMessage(message.getChatId(), "Time format is incorrect. Please, try again");
                return;
            }

            ActivityDto payload = stateMachine
                    .getStatePayload(message.getChatId(), ActivityCreate.GROUP, ActivityDto.class)
                    .orElseThrow();
            payload.setTime(message.getText());

            stateMachine.updateStateData(message.getChatId(), ActivityCreate.GROUP, payload);
            stateMachine.setState(message.getChatId(), ActivityCreate.GET_LOCATION);
            telegramService.sendMessage(message.getChatId(), "Enter the activity location");
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            telegramService.sendMessage(message.getChatId(), "Activity data wasn't saved. Error occurred");
        }
    }

    @Override
    public State getState() {
        return ActivityCreate.GET_TIME;
    }
}

