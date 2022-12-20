package edu.kmaooad.app.course.bulk_import;

import edu.kmaooad.core.Handler;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;


@Component
public class CoursesImportHandler implements Handler {

    private final TelegramService telegramService;

    public CoursesImportHandler(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {
        try {
            stateMachine.setState(message.getChatId(), CoursesImport.GET_FILE);
            telegramService.sendMessage(message.getChatId(), "Send csv file:");
        } catch (StateMachineException e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(message.getChatId(), "Error occurred!");
        }
    }

    @Override
    public List<String> getCommands() {
        return List.of("import_courses");
    }
}