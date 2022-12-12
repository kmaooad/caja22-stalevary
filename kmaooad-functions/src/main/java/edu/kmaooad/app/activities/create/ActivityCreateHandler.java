package edu.kmaooad.app.activities.create;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class ActivityCreateHandler implements Handler {

    private final TelegramService telegramService;

    public ActivityCreateHandler(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            stateMachine.setState(message.getChatId(), ActivityCreate.GET_TITLE);
            telegramService.sendMessage(message.getChatId(), "Enter the activity title");
        } catch (StateMachineException e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public List<String> getCommands() {
        return List.of("createactivity");
    }
}
