package edu.kmaooad.app.course.bulk_import_projects;


import edu.kmaooad.app.activities.Utils;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.model.CourseEntity;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Component
public class CourseProjectsImportHandler implements Handler {

    private final TelegramService telegramService;
    private final CourseService courseService;

    public CourseProjectsImportHandler(TelegramService telegramService, CourseService courseService) {
        this.telegramService = telegramService;
        this.courseService = courseService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {

            List<CourseEntity> entities = courseService.getCourses();
            InlineKeyboardMarkup kb = Utils.getCoursesKeyboard(entities);

            stateMachine.setState(message.getChatId(), CourseProjectsImport.GET_COURSE_ID);
            telegramService.sendMessage(message.getChatId(), "Select course to update:", kb);

        } catch (StateMachineException e) {

            System.err.println(e.getMessage());
            telegramService.sendMessage(message.getChatId(), "Error occurred!");

        }
    }

    @Override
    public List<String> getCommands() {
        return List.of("import_course_project");
    }
}
