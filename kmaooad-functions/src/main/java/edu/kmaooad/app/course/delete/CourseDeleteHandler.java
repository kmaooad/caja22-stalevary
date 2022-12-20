package edu.kmaooad.app.course.delete;

import edu.kmaooad.app.activities.Utils;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.model.CourseEntity;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourseDeleteHandler implements Handler {

    private final TelegramService telegramService;
    private final CourseService courseService;

    public CourseDeleteHandler(TelegramService telegramService, CourseService courseService) {
        this.telegramService = telegramService;
        this.courseService = courseService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            List<CourseEntity> entities = courseService.getCourses();
            InlineKeyboardMarkup kb = Utils.getCoursesKeyboard(entities);

            stateMachine.setState(message.getChatId(), CourseDelete.GET_ID);
            telegramService.sendMessage(message.getChatId(), "Pick the course:", kb);
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public List<String> getCommands() {
        return List.of("deletecourse");
    }
}
