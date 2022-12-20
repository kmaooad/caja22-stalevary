package edu.kmaooad.app.course.update;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.CourseUpdateDto;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CourseUpdateGetField implements Handler {

    private final TelegramService telegramService;

    public CourseUpdateGetField(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {
            CourseUpdateDto payload = stateMachine
                    .getStatePayload(callbackQuery.getMessage().getChatId(), CourseUpdate.GROUP, CourseUpdateDto.class)
                    .orElseThrow();

            payload.setField(callbackQuery.getData());
            stateMachine.updateStateData(callbackQuery.getMessage().getChatId(), CourseUpdate.GROUP, payload);

            stateMachine.setState(callbackQuery.getMessage().getChatId(), CourseUpdate.GET_VALUE);
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Enter a new value");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CourseUpdate.GET_FIELD;
    }
}

