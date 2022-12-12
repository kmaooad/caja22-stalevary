package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.activities.create.*;
import edu.kmaooad.app.activities.list.ActivitiesListHandler;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityDto;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

public class ActivityListHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final ActivityService activityService = mock(ActivityService.class);

    private final ActivitiesListHandler activitiesListHandler = new ActivitiesListHandler(telegramService, activityService);

    @Test
    void shouldReturnList_onListHandler() throws StateMachineException {
        activitiesListHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(activityService, times(1)).getUserActivities(any());
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
