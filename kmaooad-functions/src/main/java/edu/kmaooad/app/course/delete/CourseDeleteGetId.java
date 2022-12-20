package edu.kmaooad.app.course.delete;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CourseDeleteGetId implements Handler {

    private final TelegramService telegramService;
    private final CourseService courseService;

    public CourseDeleteGetId(TelegramService telegramService, CourseService activityService) {
        this.telegramService = telegramService;
        this.courseService = activityService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {
            String courseId = callbackQuery.getData();
            courseService.deleteCourse(courseId);

            stateMachine.setState(callbackQuery.getMessage().getChatId(), new State.Any());
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Course was successfully deleted!");
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CourseDelete.GET_ID;
    }
}
