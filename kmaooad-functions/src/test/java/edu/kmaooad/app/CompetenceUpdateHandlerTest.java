package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.app.competence.update.*;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.dto.CompetenceUpdateDto;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
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

public class CompetenceUpdateHandlerTest {

    private final TelegramService telegramService = mock(TelegramService.class);

    private final StateMachine stateMachine = mock(StateMachine.class);

    private final ActivityService activityService = mock(ActivityService.class);

    private final CompetenceUpdateHandler competenceUpdateHandler = new CompetenceUpdateHandler(telegramService, activityService);
    private final CompetenceUpdateGetCompetences competenceUpdateGetCompetences = new CompetenceUpdateGetCompetences(telegramService, activityService);
    private final CompetenceUpdateGetValue competenceUpdateGetValue = new CompetenceUpdateGetValue(telegramService, activityService);
    private final CompetenceUpdateValue competenceUpdateValue = new CompetenceUpdateValue(telegramService, activityService);

    @Test
    void shouldSendOptions_onUpdateHandler() throws StateMachineException {
        competenceUpdateHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(activityService, times(1)).getUserActivities(any());
        verify(stateMachine, times(1)).setState(any(), eq(CompetenceUpdate.GET_COMPETENCES));
        verify(telegramService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onUpdateHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        competenceUpdateHandler.handle(Objects.requireNonNull(message()), stateMachine);

        verify(activityService, times(1)).getUserActivities(any());
        verify(stateMachine, times(1)).setState(384859024L, CompetenceUpdate.GET_COMPETENCES);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldUpdateState_onGetCompetencesHandler() throws StateMachineException {
        ArrayList<ActivityEntity> activityEntities = new ArrayList<>();
        ArrayList<String> competences = new ArrayList<>();
        competences.add("some");
        activityEntities.add(new ActivityEntity("someid", null, null, null, null, null, null, competences));

        when(activityService.getUserActivities(any()))
                .thenReturn(activityEntities);

        competenceUpdateGetCompetences.handle(callbackQuery("someid"), stateMachine);

        verify(stateMachine, times(1)).updateStateData(eq(384859024L), eq(CompetenceUpdate.GROUP), any());
        verify(stateMachine, times(1)).setState(any(), eq(CompetenceUpdate.GET_VALUE));
        verify(telegramService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void shouldNotUpdateState_onGetCompetencesHandler() throws StateMachineException {
        ArrayList<ActivityEntity> activityEntities = new ArrayList<>();
        ArrayList<String> competences = new ArrayList<>();
        activityEntities.add(new ActivityEntity("someid", null, null, null, null, null, null, competences));

        when(activityService.getUserActivities(any()))
                .thenReturn(activityEntities);

        competenceUpdateGetCompetences.handle(callbackQuery("someid"), stateMachine);

        verify(stateMachine, times(1)).setState(any(), eq(new State.Any()));
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onGetCompetences() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .setState(anyLong(), any());

        competenceUpdateGetCompetences.handle(callbackQuery("data"), stateMachine);

        verify(stateMachine, times(0)).setState(anyLong(), eq(new State.Any()));
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldChangeState_onGetValueHandler() throws StateMachineException {
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
        competenceUpdateGetValue.handle(callbackQuery("some"), stateMachine);

        verify(stateMachine, times(1)).updateStateData(eq(384859024L), eq(CompetenceUpdate.GROUP), any());
        verify(stateMachine, times(1)).setState(384859024L, CompetenceUpdate.UPDATE_VALUE);
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onGetValue() throws StateMachineException {
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(new ActivityUpdateDto("123", null, null, null)));

        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .updateStateData(anyLong(), any(), any());

        competenceUpdateGetValue.handle(callbackQuery("title"), stateMachine);

        verify(stateMachine, times(1)).getStatePayload(384859024L, CompetenceUpdate.GROUP, CompetenceUpdateDto.class);
        verify(stateMachine, times(0)).setState(anyLong(), eq(new State.Any()));
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldChangeState_onUpdateValueHandler() throws StateMachineException {
        ArrayList<String> competences = new ArrayList<>();
        competences.add("some");
        ActivityEntity entity = new ActivityEntity("someid", null, null, null, null, null, null, competences);
        ActivityUpdateDto activityUpdateDto = new ActivityUpdateDto();
        activityUpdateDto.setActivityId("someid");

        CompetenceUpdateDto competenceUpdateDto = new CompetenceUpdateDto();
        competenceUpdateDto.setActivityUpdateDto(activityUpdateDto);
        competenceUpdateDto.setEntity(entity);
        competenceUpdateDto.setCompetence("some");
        when(stateMachine.getStatePayload(any(), any(), any()))
                .thenReturn(Optional.of(competenceUpdateDto));
        competenceUpdateValue.handle(Objects.requireNonNull(withText("newSome")), stateMachine);

        verify(stateMachine, times(1)).setState(384859024L, new State.Any());
        verify(telegramService, times(1)).sendMessage(any(), any());
    }

    @Test
    void shouldCatchExceptionOnUpdateState_onUpdateValueHandler() throws StateMachineException {
        doThrow(new StateMachineException(""))
                .when(stateMachine)
                .updateStateData(anyLong(), any(), any());

        competenceUpdateValue.handle(Objects.requireNonNull(withText("newSome")), stateMachine);

        verify(stateMachine, times(0)).setState(anyLong(), eq(new State.Any()));
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
