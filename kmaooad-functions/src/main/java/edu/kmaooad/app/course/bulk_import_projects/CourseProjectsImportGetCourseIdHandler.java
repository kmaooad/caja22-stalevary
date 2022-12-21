package edu.kmaooad.app.course.bulk_import_projects;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CourseProjectsImportGetCourseIdHandler implements Handler {

    private final TelegramService telegramService;
    private final CourseService courseService;

    public CourseProjectsImportGetCourseIdHandler(TelegramService telegramService, CourseService courseService) {
        this.telegramService = telegramService;
        this.courseService = courseService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {

            String courseId = callbackQuery.getData();
            courseService.getCourse(courseId);

            stateMachine.updateStateData(callbackQuery.getMessage().getChatId(), CourseProjectsImport.GROUP, courseId);
            stateMachine.setState(callbackQuery.getMessage().getChatId(), CourseProjectsImport.GET_FILE);

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Send csv file with projects in this format (title, description, requirements, time):");

        } catch (Exception e) {

            System.err.println(e.getMessage());
            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Incorrect id provided!");

        }
    }

    @Override
    public State getState() {
        return CourseProjectsImport.GET_COURSE_ID;
    }
}