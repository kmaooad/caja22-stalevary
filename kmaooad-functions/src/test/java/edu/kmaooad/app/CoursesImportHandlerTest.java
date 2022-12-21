package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.course.bulk_import.CoursesImport;
import edu.kmaooad.app.course.bulk_import.CoursesImportFileHandler;
import edu.kmaooad.app.course.bulk_import.CoursesImportHandler;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CoursesImportHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final CourseService courseService = mock(CourseService.class);

    private final CoursesImportHandler coursesImportHandler = new CoursesImportHandler(telegramService);

    private final CoursesImportFileHandler coursesImportFileHandler = new CoursesImportFileHandler(telegramService, courseService);

    @Test
    void shouldUpdateState_onImportHandler() throws StateMachineException {
        coursesImportHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CoursesImport.GET_FILE);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldThrowException_onImportHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        coursesImportHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CoursesImport.GET_FILE);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldSaveCoursesFromFile() {
        File file = new File();
        file.setFilePath("courses.csv");
        file.setFileUniqueId("");

        Document document = new Document();
        document.setFileId("");

        Message message = message();
        message.setDocument(document);

        ClassLoader classLoader = getClass().getClassLoader();
        java.io.File csvFile = new java.io.File(classLoader.getResource("files/courses.csv").getFile());

        when(telegramService.getFile(any()))
                .thenReturn(file);
        when(telegramService.downloadFile(any(), any()))
                .thenReturn(csvFile);

        coursesImportFileHandler.handle(message, stateMachine);

        verify(courseService, times(2)).createCourse(any());
    }

    @Test
    void shouldSendException_onIncorrectFile() {
        File file = new File();
        file.setFilePath("courses.csv");
        file.setFileUniqueId("");

        Document document = new Document();
        document.setFileId("");

        Message message = message();
        message.setDocument(document);

        ClassLoader classLoader = getClass().getClassLoader();
        java.io.File csvFile = new java.io.File(classLoader.getResource("files/courses_incorrect.csv").getFile());

        when(telegramService.getFile(any()))
                .thenReturn(file);
        when(telegramService.downloadFile(any(), any()))
                .thenReturn(csvFile);

        coursesImportFileHandler.handle(message, stateMachine);

        verify(courseService, times(0)).createCourse(any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    private Message message() {
        String updateJson = """
                {
                   "message_id":207,
                   "from":{
                      "id":384859024,
                      "is_bot":false,
                      "first_name":"Vlad",
                      "last_name":"Kozyr",
                      "username":"vladyslav_kozyr",
                      "language_code":"en"
                   },
                   "chat":{
                      "id":384859024,
                      "first_name":"Vlad",
                      "last_name":"Kozyr",
                      "username":"vladyslav_kozyr",
                      "type":"private"
                   },
                   "date":1669839096,
                   "text":"/course",
                   "entities":[
                      {
                         "offset":0,
                         "length":7,
                         "type":"bot_command"
                      }
                   ]
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(updateJson, Message.class);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }
}
