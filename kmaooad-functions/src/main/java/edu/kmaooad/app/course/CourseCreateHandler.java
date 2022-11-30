package edu.kmaooad.app.course;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class CourseCreateHandler implements Handler {

    private final TelegramService telegramService;

    public CourseCreateHandler(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        telegramService.sendMessage(message.getChatId(), "Enter course title: ");
        stateMachine.setState(message.getChatId(), CourseCreate.GET_TITLE);
    }

    @Override
    public List<String> getCommands() {
        return List.of("course");
    }
}
