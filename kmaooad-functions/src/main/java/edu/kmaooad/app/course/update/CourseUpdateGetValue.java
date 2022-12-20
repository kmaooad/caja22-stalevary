package edu.kmaooad.app.course.update;

import edu.kmaooad.app.course.update.CourseUpdate;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.CourseUpdateDto;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CourseUpdateGetValue implements Handler {

    private final TelegramService telegramService;

    private final CourseService courseService;

    public CourseUpdateGetValue(TelegramService telegramService, CourseService courseService) {
        this.telegramService = telegramService;
        this.courseService = courseService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            CourseUpdateDto payload = stateMachine
                    .getStatePayload(message.getChatId(), CourseUpdate.GROUP, CourseUpdateDto.class)
                    .orElseThrow();

            payload.setValue(message.getText());
            courseService.updateCourse(payload);

            stateMachine.setState(message.getChatId(), new State.Any());
            telegramService.sendMessage(message.getChatId(), "Successfully updated!");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CourseUpdate.GET_VALUE;
    }
}

