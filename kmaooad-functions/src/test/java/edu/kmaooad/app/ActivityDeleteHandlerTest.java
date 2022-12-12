package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.activities.delete.ActivityDelete;
import edu.kmaooad.app.activities.delete.ActivityDeleteGetId;
import edu.kmaooad.app.activities.delete.ActivityDeleteHandler;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ActivityDeleteHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final ActivityService activityService = mock(ActivityService.class);

    private final ActivityDeleteHandler activityDeleteHandler = new ActivityDeleteHandler(telegramService, activityService);
    private final ActivityDeleteGetId activityDeleteGetId = new ActivityDeleteGetId(telegramService, activityService);

    @Test
    void shouldSendOptions_onDeleteHandler() throws StateMachineException {
        activityDeleteHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(activityService, times(1)).getUserActivities(any());
        verify(stateMachine, times(1)).setState(any(), eq(ActivityDelete.GET_ID));
        verify(telegramService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onDeleteHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        activityDeleteHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(activityService, times(1)).getUserActivities(any());
        verify(stateMachine, times(1)).setState(384859024L, ActivityDelete.GET_ID);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldDeleteActivity_onGetIdHandler() throws StateMachineException {
        activityDeleteGetId.handle(callbackQuery("123"), stateMachine);

        verify(activityService, times(1)).deleteActivity("123");
        verify(stateMachine, times(1)).setState(any(), eq(new State.Any()));
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onGetId() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        activityDeleteGetId.handle(callbackQuery("data"), stateMachine);

        verify(activityService, times(1)).deleteActivity(any());
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
