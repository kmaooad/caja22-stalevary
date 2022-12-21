package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.competence.delete.*;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.dto.CompetenceUpdateDto;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CompetenceDeleteHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final ActivityService activityService = mock(ActivityService.class);

    private final CompetenceDeleteHandler competenceDeleteHandler = new CompetenceDeleteHandler(telegramService, activityService);
    private final CompetenceDeleteValue competenceDeleteValue = new CompetenceDeleteValue(telegramService, activityService);
    private final CompetenceDeleteGetCompetences competenceDeleteGetCompetences = new CompetenceDeleteGetCompetences(telegramService, activityService);

    @Test
    void shouldSendOptions_onDeleteHandler() throws StateMachineException {
        competenceDeleteHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(activityService, times(1)).getUserActivities(any());
        verify(stateMachine, times(1)).setState(any(), eq(CompetenceDelete.GET_COMPETENCES));
        verify(telegramService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onDeleteHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        competenceDeleteHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(activityService, times(1)).getUserActivities(any());
        verify(stateMachine, times(1)).setState(384859024L, CompetenceDelete.GET_COMPETENCES);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldSendOptions_onGetCompetencesHandler() throws StateMachineException {
        ArrayList<ActivityEntity> activityEntities = new ArrayList<>();
        ArrayList<String> competences = new ArrayList<>();
        competences.add("some");
        activityEntities.add(new ActivityEntity("someid", null, null, null, null, null, null, competences));

        when(activityService.getUserActivities(any()))
                .thenReturn(activityEntities);
        competenceDeleteGetCompetences.handle(callbackQuery("someid"), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, CompetenceDelete.DELETE);
        verify(telegramService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void shouldNotSendOptions_onGetCompetencesHandler() throws StateMachineException {
        ArrayList<ActivityEntity> activityEntities = new ArrayList<>();
        ArrayList<String> competences = new ArrayList<>();
        activityEntities.add(new ActivityEntity("someid", null, null, null, null, null, null, competences));

        when(activityService.getUserActivities(any()))
                .thenReturn(activityEntities);
        competenceDeleteGetCompetences.handle(callbackQuery("someid"), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, new State.Any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onGetCompetencesHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        competenceDeleteGetCompetences.handle(callbackQuery("someid"), stateMachine);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldDelete_onDeleteValueHandler() throws StateMachineException {
        ArrayList<String> competences = new ArrayList<>();
        competences.add("some");
        ActivityEntity entity = new ActivityEntity("someid", null, null, null, null, null, null, competences);
        ActivityUpdateDto activityUpdateDto = new ActivityUpdateDto();
        activityUpdateDto.setActivityId("someid");

        CompetenceUpdateDto competenceUpdateDto = new CompetenceUpdateDto();
        competenceUpdateDto.setActivityUpdateDto(activityUpdateDto);
        competenceUpdateDto.setEntity(entity);
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(competenceUpdateDto));
        competenceDeleteValue.handle(callbackQuery("some"), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, new State.Any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onDeleteValueHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        competenceDeleteValue.handle(callbackQuery("someid"), stateMachine);
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
