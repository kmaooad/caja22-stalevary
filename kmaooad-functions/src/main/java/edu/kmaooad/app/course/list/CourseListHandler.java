package edu.kmaooad.app.course.list;


import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.model.CourseEntity;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class CourseListHandler implements Handler {

    private final TelegramService telegramService;
    private final CourseService courseService;

    public CourseListHandler(TelegramService telegramService, CourseService courseService) {
        this.telegramService = telegramService;
        this.courseService = courseService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Courses:\n");

            List<CourseEntity> entities = courseService.getCourses();
            for (CourseEntity entity : entities) {
                sb.append("--------------------\n");
                sb.append("Id: ").append(entity.getId()).append("\n");
                sb.append("Title: ").append(entity.getTitle()).append("\n");
                sb.append("Description: ").append(entity.getDescription()).append("\n");
                sb.append("--------------------\n");
            }

            telegramService.sendMessage(message.getChatId(), sb.toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public List<String> getCommands() {
        return List.of("listcourses");
    }
}

