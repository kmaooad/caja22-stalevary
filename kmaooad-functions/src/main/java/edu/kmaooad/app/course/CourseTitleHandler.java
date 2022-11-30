package edu.kmaooad.app.course;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CourseTitleHandler implements Handler {

    private final TelegramService telegramService;

    public CourseTitleHandler(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            CourseDto payload = new CourseDto(message.getText(), null);
            stateMachine.updateStateData(message.getChatId(), CourseCreate.GROUP, payload);
        } catch (Exception exception) {
            telegramService.sendMessage(message.getChatId(), "Course don't saved: Some problem");
        } finally {
            telegramService.sendMessage(message.getChatId(), "Enter course description: ");
            stateMachine.setState(message.getChatId(), CourseCreate.GET_DESCRIPTION);
        }
    }

    @Override
    public State getState() {
        return CourseCreate.GET_TITLE;
    }
}