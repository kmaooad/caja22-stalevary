package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.course.create.CourseCreate;
import edu.kmaooad.app.course.create.CourseCreateHandler;
import edu.kmaooad.app.course.create.CourseDescriptionHandler;
import edu.kmaooad.app.course.create.CourseTitleHandler;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.service.CourseService;
import edu.kmaooad.telegram.TelegramService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CourseHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final CourseService courseService = mock(CourseService.class);

    private final CourseCreateHandler courseCreateHandler = new CourseCreateHandler(telegramService);
    private final CourseTitleHandler courseTitleHandler = new CourseTitleHandler(telegramService);
    private final CourseDescriptionHandler courseDescriptionHandler = new CourseDescriptionHandler(telegramService, courseService);

    @Test
    void shouldUpdateState_onCreateHandler() throws StateMachineException {

        courseCreateHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CourseCreate.GET_TITLE);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onCreateHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        courseCreateHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CourseCreate.GET_TITLE);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onTitleHandler() throws StateMachineException {

        courseTitleHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1))
                .setState(384859024L, CourseCreate.GET_DESCRIPTION);
        verify(stateMachine, times(1))
                .updateStateData(eq(384859024L), eq(CourseCreate.GROUP), refEq(new CourseDto("/course", null)));
        verify(telegramService, times(1))
                .sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onTitleHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        courseTitleHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CourseCreate.GET_DESCRIPTION);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnSaveStatePayload_onTitleHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .updateStateData(anyLong(), any(), any());

        courseTitleHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1))
                .updateStateData(eq(384859024L), eq(CourseCreate.GROUP), refEq(new CourseDto("/course", null)));
        verify(stateMachine, times(0))
                .setState(anyLong(), any());
        verify(telegramService, times(1))
                .sendMessage(any(), any());
    }

    @Test
    void shouldSaveCourse_onDescriptionHandler() throws StateMachineException {

        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new CourseDto("TITLE", null)));

        courseDescriptionHandler.handle(Objects.requireNonNull(message()), stateMachine);

        ArgumentCaptor<CourseDto> argument = ArgumentCaptor.forClass(CourseDto.class);
        verify(courseService, times(1))
                .createCourse(argument.capture());
        assertEquals("/course", argument.getValue().getDescription());

        verify(stateMachine, times(1))
                .setState(384859024L, new State.Any());
        verify(telegramService, times(1))
                .sendMessage(any(), any());
    }

    @Test
    void shouldThrowExceptionOnGetPayload_onDescriptionHandler() throws StateMachineException {

        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.empty());

        courseDescriptionHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(courseService, times(0)).createCourse(any());
        verify(stateMachine, times(0)).setState(any(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldThrowExceptionOnSaveState_onDescriptionHandler() throws StateMachineException {

        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new CourseDto("TITLE", null)));
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        courseDescriptionHandler.handle(Objects.requireNonNull(message()), stateMachine);

        ArgumentCaptor<CourseDto> argument = ArgumentCaptor.forClass(CourseDto.class);
        verify(courseService, times(1))
                .createCourse(argument.capture());
        assertEquals("/course", argument.getValue().getDescription());

        verify(stateMachine, times(1))
                .setState(384859024L, new State.Any());
        verify(telegramService, times(1))
                .sendMessage(any(), any());
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

    private final State.Group group = new State.Group() {
        @Override
        public String group() {
            return "key";
        }
    };
}
