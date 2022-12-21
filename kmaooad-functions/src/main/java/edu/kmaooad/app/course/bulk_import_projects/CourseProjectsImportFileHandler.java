package edu.kmaooad.app.course.bulk_import_projects;

import com.opencsv.bean.CsvToBeanBuilder;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.CourseProjectDto;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.FileReader;
import java.util.List;
import java.util.Optional;

@Component
public class CourseProjectsImportFileHandler implements Handler {

    private final TelegramService telegramService;
    private final CourseService courseService;

    public CourseProjectsImportFileHandler(TelegramService telegramService, CourseService courseService) {
        this.telegramService = telegramService;
        this.courseService = courseService;
    }

    @Override
    public void handle(Message message, StateMachine stateMachine) {

        try {
            String fileID = message.getDocument().getFileId();
            GetFile uploadedFile = new GetFile();
            uploadedFile.setFileId(fileID);
            File file = telegramService.getFile(uploadedFile);

            java.io.File csvFile = telegramService.downloadFile(file, file.getFileUniqueId() + message.getDocument().getFileName());
            List<CourseProjectDto> courseProjectList = new CsvToBeanBuilder(new FileReader(csvFile))
                    .withType(CourseProjectDto.class)
                    .build()
                    .parse();

            courseProjectList = courseProjectList
                    .stream()
                    .filter(dto -> dto.getTitle() != null
                            && dto.getDescription() != null
                            && dto.getRequirements() != null
                    ).toList();

            Optional<String> courseId = stateMachine.getStatePayload(message.getChatId(), CourseProjectsImport.GROUP, String.class);
            if (courseId.isPresent()) {
                courseService.addCourseProjects(courseId.get(), courseProjectList);
                telegramService.sendMessage(message.getChatId(), "Courses uploaded!");
            } else {
                telegramService.sendMessage(message.getChatId(), "Something went wrong, try again!");
            }
        } catch (Exception exception) {
            telegramService.sendMessage(message.getChatId(), "Incorrect CSV file: " + exception.getMessage());
        }
    }

    @Override
    public State getState() {
        return CourseProjectsImport.GET_FILE;
    }
}
