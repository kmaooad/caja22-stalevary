package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.competence.create.*;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityDto;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CompetencesCreateHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final ActivityService activityService = mock(ActivityService.class);

    private final CompetenceCreateHandler competenceCreateHandler = new CompetenceCreateHandler(telegramService, activityService);
    private final CompetenceCreateGetActivityId competenceCreateGetActivityId = new CompetenceCreateGetActivityId(telegramService);
    private final CompetenceCreateGetCompetences competenceCreateGetCompetences = new CompetenceCreateGetCompetences(telegramService, activityService);

    @Test
    void shouldUpdateState_onCreateHandler() throws StateMachineException {

        competenceCreateHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CompetenceCreate.GET_ACTIVITY_ID);
        verify(telegramService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onCreateHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        competenceCreateHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CompetenceCreate.GET_ACTIVITY_ID);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onGetActivityIdHandler() throws StateMachineException {

        competenceCreateGetActivityId.handle(callbackQuery("someid"), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CompetenceCreate.GET_COMPETENCES);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onGetActivityIdHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        competenceCreateGetActivityId.handle(callbackQuery("someid"), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CompetenceCreate.GET_COMPETENCES);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onGetCompetencesHandler() throws StateMachineException {

        competenceCreateGetCompetences.handle(Objects.requireNonNull(withText("Some competence")), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, CompetenceCreate.GROUP, ActivityUpdateDto.class);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onGetCompetencesHandler() throws StateMachineException {

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        competenceCreateGetCompetences.handle(Objects.requireNonNull(withText("Some competence")), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, CompetenceCreate.GROUP, ActivityUpdateDto.class);
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

