package edu.kmaooad.app.activities.create;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityDto;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class ActivityCreateGetDescription implements Handler {
    private final TelegramService telegramService;

    public ActivityCreateGetDescription(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            ActivityDto payload = stateMachine
                    .getStatePayload(message.getChatId(), ActivityCreate.GROUP, ActivityDto.class)
                    .orElseThrow();
            payload.setDescription(message.getText());

            stateMachine.updateStateData(message.getChatId(), ActivityCreate.GROUP, payload);
            stateMachine.setState(message.getChatId(), ActivityCreate.GET_DATE);
            telegramService.sendMessage(message.getChatId(), "Enter the activity date in dd.mm.yy format");
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            telegramService.sendMessage(message.getChatId(), "Activity data wasn't saved. Error occurred");
        }
    }

    @Override
    public State getState() {
        return ActivityCreate.GET_DESCRIPTION;
    }
}

