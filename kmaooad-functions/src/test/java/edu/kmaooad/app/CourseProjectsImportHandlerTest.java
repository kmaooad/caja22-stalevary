package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.course.bulk_import_projects.CourseProjectsImport;
import edu.kmaooad.app.course.bulk_import_projects.CourseProjectsImportFileHandler;
import edu.kmaooad.app.course.bulk_import_projects.CourseProjectsImportGetCourseIdHandler;
import edu.kmaooad.app.course.bulk_import_projects.CourseProjectsImportHandler;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.CourseEntity;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class CourseProjectsImportHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final CourseService courseService = mock(CourseService.class);

    private final CourseProjectsImportHandler courseProjectsImportHandler = new CourseProjectsImportHandler(telegramService, courseService);

    private final CourseProjectsImportGetCourseIdHandler courseProjectsImportGetCourseIdHandler = new CourseProjectsImportGetCourseIdHandler(telegramService, courseService);

    private final CourseProjectsImportFileHandler courseProjectsImportFileHandler = new CourseProjectsImportFileHandler(telegramService, courseService);

    @Test
    void shouldUpdateState_onImportHandler() throws StateMachineException {
        courseProjectsImportHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CourseProjectsImport.GET_COURSE_ID);
        verify(telegramService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void shouldThrowException_onImportHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        courseProjectsImportHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CourseProjectsImport.GET_COURSE_ID);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateStateAndPayload_onGetCourseIdHandler() throws StateMachineException, IncorrectIdException {
        when(courseService.getCourse(any()))
                .thenReturn(new CourseEntity("1", "title", "desc", new ArrayList<>()));

        courseProjectsImportGetCourseIdHandler.handle(callbackQuery(message(), "1"), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CourseProjectsImport.GET_FILE);
        verify(stateMachine, times(1)).updateStateData(384859024L, CourseProjectsImport.GROUP, "1");
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldThrowForIncorrectId_onGetCourseIdHandler() throws StateMachineException, IncorrectIdException {
        doThrow(new IncorrectIdException())
                .when(courseService)
                .getCourse(any());

        courseProjectsImportGetCourseIdHandler.handle(callbackQuery(message(), "1"), stateMachine);

        verify(stateMachine, times(0)).setState(anyLong(), any());
        verify(stateMachine, times(0)).updateStateData(anyLong(), any(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldSaveCourseProjectsFromFile() throws IncorrectIdException {
        File file = new File();
        file.setFilePath("courses.csv");
        file.setFileUniqueId("");

        Document document = new Document();
        document.setFileId("");

        Message message = message();
        message.setDocument(document);

        ClassLoader classLoader = getClass().getClassLoader();
        java.io.File csvFile = new java.io.File(classLoader.getResource("files/course_projects.csv").getFile());

        when(telegramService.getFile(any()))
                .thenReturn(file);
        when(telegramService.downloadFile(any(), any()))
                .thenReturn(csvFile);
        when(stateMachine.getStatePayload(anyLong(), any(), any()))
                .thenReturn(Optional.of("1"));

        courseProjectsImportFileHandler.handle(message, stateMachine);

        verify(courseService, times(1)).addCourseProjects(any(), any());
    }

    @Test
    void shouldSendException_onIncorrectFile() throws IncorrectIdException {
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

        courseProjectsImportFileHandler.handle(message, stateMachine);

        verify(courseService, times(0)).addCourseProjects(any(), any());
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

    private CallbackQuery callbackQuery(Message message, String data) {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(data);
        callbackQuery.setMessage(message);
        return callbackQuery;
    }
}
