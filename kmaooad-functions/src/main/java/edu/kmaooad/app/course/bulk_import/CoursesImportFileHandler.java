package edu.kmaooad.app.course.bulk_import;

import com.opencsv.bean.CsvToBeanBuilder;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.FileReader;
import java.util.List;

@Component
public class CoursesImportFileHandler implements Handler {

    private final TelegramService telegramService;
    private final CourseService courseService;

    public CoursesImportFileHandler(TelegramService telegramService, CourseService courseService) {
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

            java.io.File csvFile = telegramService.downloadFile(file, file.getFileUniqueId() + file.getFilePath());

            List<CourseDto> courseDtoList = new CsvToBeanBuilder(new FileReader(csvFile))
                    .withType(CourseDto.class)
                    .build()
                    .parse();

            courseDtoList = courseDtoList
                    .stream()
                    .filter(dto -> dto.getTitle() != null
                            && dto.getDescription() != null
                    ).toList();

            courseDtoList.forEach(courseService::createCourse);
            stateMachine.setState(message.getChatId(), new State.Any());
            telegramService.sendMessage(message.getChatId(), courseDtoList.size() + " Courses uploaded!");
        } catch (Exception exception) {
            telegramService.sendMessage(message.getChatId(), "Incorrect CSV file: " + exception.getMessage());
        }

    }

    @Override
    public State getState() {
        return CoursesImport.GET_FILE;
    }
}