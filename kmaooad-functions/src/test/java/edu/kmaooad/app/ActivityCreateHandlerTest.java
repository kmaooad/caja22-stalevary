package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.activities.create.*;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityDto;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ActivityCreateHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final ActivityService activityService = mock(ActivityService.class);

    private final ActivityCreateHandler activityCreateHandler = new ActivityCreateHandler(telegramService);
    private final ActivityCreateGetTitle activityTitleHandler = new ActivityCreateGetTitle(telegramService);
    private final ActivityCreateGetDescription activityDescriptionHandler = new ActivityCreateGetDescription(telegramService);
    private final ActivityCreateGetDate activityDateHandler = new ActivityCreateGetDate(telegramService);
    private final ActivityCreateGetTime activityTimeHandler = new ActivityCreateGetTime(telegramService);
    private final ActivityCreateGetLocation activityLocationHandler = new ActivityCreateGetLocation(telegramService, activityService);

    @Test
    void shouldUpdateState_onCreateHandler() throws StateMachineException {

        activityCreateHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, ActivityCreate.GET_TITLE);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onCreateHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        activityCreateHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, ActivityCreate.GET_TITLE);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onTitleHandler() throws StateMachineException {
        activityTitleHandler.handle(Objects.requireNonNull(withText("title")), stateMachine);

        verify(stateMachine, times(1))
                .setState(384859024L, ActivityCreate.GET_DESCRIPTION);
        verify(stateMachine, times(1))
                .updateStateData(eq(384859024L), eq(ActivityCreate.GROUP), refEq(new ActivityDto("384859024", "title", null, null, null, null)));
        verify(telegramService, times(1))
                .sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onTitleHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        activityTitleHandler.handle(Objects.requireNonNull(withText("title")), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, ActivityCreate.GET_DESCRIPTION);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnSaveStatePayload_onTitleHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .updateStateData(anyLong(), any(), any());

        activityTitleHandler.handle(Objects.requireNonNull(withText("title")), stateMachine);

        verify(stateMachine, times(1))
                .updateStateData(eq(384859024L), eq(ActivityCreate.GROUP), refEq(new ActivityDto("384859024", "title", null, null, null, null)));
        verify(stateMachine, times(0))
                .setState(anyLong(), any());
        verify(telegramService, times(1))
                .sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onDescriptionHandler() throws StateMachineException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new ActivityDto("384859024", "title", null, null, null, null)));

        activityDescriptionHandler.handle(Objects.requireNonNull(withText("description")), stateMachine);

        verify(stateMachine, times(1))
                .setState(384859024L, ActivityCreate.GET_DATE);
        verify(stateMachine, times(1))
                .updateStateData(eq(384859024L), eq(ActivityCreate.GROUP), refEq(new ActivityDto("384859024", "title", "description", null, null, null)));
        verify(telegramService, times(1))
                .sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onDescriptionHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        activityDescriptionHandler.handle(Objects.requireNonNull(withText("description")), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, ActivityCreate.GROUP, ActivityDto.class);
        verify(stateMachine, times(0)).setState(anyLong(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnSaveStatePayload_onDescriptionHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .updateStateData(anyLong(), any(), any());

        activityDescriptionHandler.handle(Objects.requireNonNull(withText("description")), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, ActivityCreate.GROUP, ActivityDto.class);
        verify(stateMachine, times(0)).updateStateData(anyLong(), any(), any());
        verify(stateMachine, times(0)).setState(anyLong(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onDateHandler() throws StateMachineException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new ActivityDto("384859024", "title", "description", null, null, null)));


        activityDateHandler.handle(Objects.requireNonNull(withText("11.12.22")), stateMachine);

        verify(stateMachine, times(1))
                .setState(384859024L, ActivityCreate.GET_TIME);
        verify(stateMachine, times(1))
                .updateStateData(eq(384859024L), eq(ActivityCreate.GROUP), refEq(new ActivityDto("384859024", "title", "description", "11.12.22", null, null)));
        verify(telegramService, times(1))
                .sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onDateHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        activityDateHandler.handle(Objects.requireNonNull(withText("11.12.22")), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, ActivityCreate.GROUP, ActivityDto.class);
        verify(stateMachine, times(0)).setState(anyLong(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnSaveStatePayload_onDateHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .updateStateData(anyLong(), any(), any());

        activityDateHandler.handle(Objects.requireNonNull(withText("11.12.22")), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, ActivityCreate.GROUP, ActivityDto.class);
        verify(stateMachine, times(0)).updateStateData(anyLong(), any(), any());
        verify(stateMachine, times(0)).setState(anyLong(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldFailValidation_onDateHandler() throws StateMachineException {
        activityDateHandler.handle(Objects.requireNonNull(withText("11/12/22")), stateMachine);

        verify(stateMachine, times(0)).getStatePayload(anyLong(), any(), any());
        verify(stateMachine, times(0)).updateStateData(anyLong(), any(), any());
        verify(stateMachine, times(0)).setState(anyLong(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onTimeHandler() throws StateMachineException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new ActivityDto("384859024", "title", "description", "11.12.22", null, null)));


        activityTimeHandler.handle(Objects.requireNonNull(withText("11:12")), stateMachine);

        verify(stateMachine, times(1))
                .setState(384859024L, ActivityCreate.GET_LOCATION);
        verify(stateMachine, times(1))
                .updateStateData(eq(384859024L), eq(ActivityCreate.GROUP), refEq(new ActivityDto("384859024", "title", "description", "11.12.22", "11:12", null)));
        verify(telegramService, times(1))
                .sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onTimeHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        activityTimeHandler.handle(Objects.requireNonNull(withText("11:12")), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, ActivityCreate.GROUP, ActivityDto.class);
        verify(stateMachine, times(0)).setState(anyLong(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnSaveStatePayload_onTimeHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .updateStateData(anyLong(), any(), any());

        activityTimeHandler.handle(Objects.requireNonNull(withText("11:12")), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, ActivityCreate.GROUP, ActivityDto.class);
        verify(stateMachine, times(0)).updateStateData(anyLong(), any(), any());
        verify(stateMachine, times(0)).setState(anyLong(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldFailValidation_onTimeHandler() throws StateMachineException {
        activityDateHandler.handle(Objects.requireNonNull(withText("11.12")), stateMachine);

        verify(stateMachine, times(0)).getStatePayload(anyLong(), any(), any());
        verify(stateMachine, times(0)).updateStateData(anyLong(), any(), any());
        verify(stateMachine, times(0)).setState(anyLong(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldSaveActivity_onLocationHandler() throws StateMachineException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new ActivityDto("384859024", "title", "description", "11.12.22", "11:12", null)));

        activityLocationHandler.handle(Objects.requireNonNull(withText("location")), stateMachine);

        ArgumentCaptor<ActivityDto> argument = ArgumentCaptor.forClass(ActivityDto.class);
        verify(activityService, times(1))
                .createActivity(argument.capture());
        assertEquals("title", argument.getValue().getTitle());

        verify(stateMachine, times(1))
                .setState(384859024L, new State.Any());
        verify(telegramService, times(1))
                .sendMessage(any(), any());
    }

    @Test
    void shouldThrowExceptionOnGetPayload_onDescriptionHandler() throws StateMachineException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.empty());

        activityLocationHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(activityService, times(0)).createActivity(any());
        verify(stateMachine, times(0)).setState(any(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldThrowExceptionOnSaveState_onDescriptionHandler() throws StateMachineException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new ActivityDto("384859024", "title", "description", "11.12.22", "11:12", null)));

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        activityLocationHandler.handle(Objects.requireNonNull(message()), stateMachine);

        ArgumentCaptor<ActivityDto> argument = ArgumentCaptor.forClass(ActivityDto.class);
        verify(activityService, times(1)).createActivity(argument.capture());
        assertEquals("description", argument.getValue().getDescription());

        verify(stateMachine, times(1))
                .setState(384859024L, new State.Any());
        verify(telegramService, times(1))
                .sendMessage(any(), any());
    }

    private JSONObject messageJSON() {
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
        return new JSONObject(updateJson);
    }

    private Message message() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(messageJSON().toString(), Message.class);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }

    private Message withText(String text) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JSONObject json = messageJSON();
            json.put("text", text);
            return mapper.readValue(json.toString(), Message.class);
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

