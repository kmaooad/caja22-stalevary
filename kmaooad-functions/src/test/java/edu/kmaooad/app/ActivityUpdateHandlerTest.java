package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.activities.delete.ActivityDelete;
import edu.kmaooad.app.activities.delete.ActivityDeleteGetId;
import edu.kmaooad.app.activities.delete.ActivityDeleteHandler;
import edu.kmaooad.app.activities.update.*;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ActivityUpdateHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final ActivityService activityService = mock(ActivityService.class);

    private final ActivityUpdateHandler activityUpdateHandler = new ActivityUpdateHandler(telegramService, activityService);
    private final ActivityUpdateGetId activityUpdateGetId = new ActivityUpdateGetId(telegramService);
    private final ActivityUpdateGetField activityUpdateGetField = new ActivityUpdateGetField(telegramService);
    private final ActivityUpdateGetValue activityUpdateGetValue = new ActivityUpdateGetValue(telegramService, activityService);

    @Test
    void shouldSendOptions_onUpdateHandler() throws StateMachineException {
        activityUpdateHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(activityService, times(1)).getUserActivities(any());
        verify(stateMachine, times(1)).setState(any(), eq(ActivityUpdate.GET_ID));
        verify(telegramService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onUpdateHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        activityUpdateHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(activityService, times(1)).getUserActivities(any());
        verify(stateMachine, times(1)).setState(384859024L, ActivityUpdate.GET_ID);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onGetIdHandler() throws StateMachineException {
        activityUpdateGetId.handle(callbackQuery("123"), stateMachine);

        verify(stateMachine, times(1)).updateStateData(eq(384859024L), eq(ActivityUpdate.GROUP), any());
        verify(stateMachine, times(1)).setState(any(), eq(ActivityUpdate.GET_FIELD));
        verify(telegramService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onGetId() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        activityUpdateGetId.handle(callbackQuery("data"), stateMachine);

        verify(stateMachine, times(1)).updateStateData(eq(384859024L), eq(ActivityUpdate.GROUP), any());
        verify(stateMachine, times(0)).setState(anyLong(), eq(new State.Any()));
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onGetField() throws StateMachineException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new ActivityUpdateDto("123", null, null)));

        activityUpdateGetField.handle(callbackQuery("title"), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, ActivityUpdate.GROUP, ActivityUpdateDto.class);
        verify(stateMachine, times(1)).updateStateData(eq(384859024L), eq(ActivityUpdate.GROUP), any());
        verify(stateMachine, times(1)).setState(any(), eq(ActivityUpdate.GET_VALUE));
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onGetField() throws StateMachineException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new ActivityUpdateDto("123", null, null)));

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .updateStateData(anyLong(), any(), any());

        activityUpdateGetField.handle(callbackQuery("title"), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, ActivityUpdate.GROUP, ActivityUpdateDto.class);
        verify(stateMachine, times(1)).updateStateData(eq(384859024L), eq(ActivityUpdate.GROUP), any());
        verify(stateMachine, times(0)).setState(anyLong(), eq(new State.Any()));
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateActivity_onGetValue() throws StateMachineException, IncorrectIdException, JsonProcessingException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new ActivityUpdateDto("123", null, null)));

        activityUpdateGetValue.handle(withText("new title"), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, ActivityUpdate.GROUP, ActivityUpdateDto.class);
        verify(activityService, times(1)).updateActivity(any());
        verify(stateMachine, times(1)).setState(any(), eq(new State.Any()));
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateActivity_onGetValue() throws StateMachineException, IncorrectIdException, JsonProcessingException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new ActivityUpdateDto("123", null, null)));

        doThrow(new IncorrectIdException())
                .when(activityService)
                .updateActivity(any());

        activityUpdateGetValue.handle(withText("new title"), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, ActivityUpdate.GROUP, ActivityUpdateDto.class);
        verify(activityService, times(1)).updateActivity(any());
        verify(stateMachine, times(0)).setState(any(), any());
        verify(telegramService, times(1)).sendMessage(any(), any());
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

    private CallbackQuery callbackQuery(String data) {
        Message message = message();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(data);
        callbackQuery.setMessage(message);
        return callbackQuery;
    }

    private final State.Group group = new State.Group() {
        @Override
        public String group() {
            return "key";
        }
    };
}
