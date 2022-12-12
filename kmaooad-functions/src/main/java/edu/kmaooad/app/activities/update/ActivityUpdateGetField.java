package edu.kmaooad.app.activities.update;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class ActivityUpdateGetField implements Handler {

    private final TelegramService telegramService;

    public ActivityUpdateGetField(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {
            ActivityUpdateDto payload = stateMachine
                    .getStatePayload(callbackQuery.getMessage().getChatId(), ActivityUpdate.GROUP, ActivityUpdateDto.class)
                    .orElseThrow();

            payload.setField(callbackQuery.getData());
            stateMachine.updateStateData(callbackQuery.getMessage().getChatId(), ActivityUpdate.GROUP, payload);

            stateMachine.setState(callbackQuery.getMessage().getChatId(), ActivityUpdate.GET_VALUE);
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Enter a new value");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return ActivityUpdate.GET_FIELD;
    }
}

