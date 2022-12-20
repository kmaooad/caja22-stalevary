package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.competence.create.CompetenceCreate;
import edu.kmaooad.app.competence.list.*;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CompetenceListHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final ActivityService activityService = mock(ActivityService.class);

    private final CompetenceListHandler competenceListHandler = new CompetenceListHandler(telegramService, activityService);
    private final CompetenceListGetCompetences competenceListGetCompetences = new CompetenceListGetCompetences(telegramService, activityService);

    @Test
    void shouldUpdateState_onListHandler() throws StateMachineException {
        competenceListHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CompetenceList.GET_COMPETENCES);
        verify(telegramService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onListHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        competenceListHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CompetenceList.GET_COMPETENCES);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onGetCompetencesHandler() throws StateMachineException {
        competenceListGetCompetences.handle(callbackQuery("someid"), stateMachine);

        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onGetCompetencesHandler() throws StateMachineException {
        competenceListGetCompetences.handle(callbackQuery("someid"), stateMachine);

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        competenceListGetCompetences.handle(Objects.requireNonNull(message()), stateMachine);

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

    private CallbackQuery callbackQuery(String data) {
        Message message = message();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(data);
        callbackQuery.setMessage(message);
        return callbackQuery;
    }
}
