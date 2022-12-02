package edu.kmaooad.app.course;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CourseDescriptionHandler implements Handler {

    private final TelegramService telegramService;
    private final CourseService courseService;

    public CourseDescriptionHandler(TelegramService telegramService, CourseService courseService) {
        this.telegramService = telegramService;
        this.courseService = courseService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            CourseDto payload = stateMachine
                    .getStatePayload(message.getChatId(), CourseCreate.GROUP, CourseDto.class)
                    .orElseThrow();

            payload.setDescription(message.getText());
            courseService.createCourse(payload);
            stateMachine.setState(message.getChatId(), new State.Any());
            telegramService.sendMessage(message.getChatId(), "Course saved");
        } catch (Exception exception) {
            telegramService.sendMessage(message.getChatId(), "Course don't saved: Some problem");
        }
    }

    @Override
    public State getState() {
        return CourseCreate.GET_DESCRIPTION;
    }
}
