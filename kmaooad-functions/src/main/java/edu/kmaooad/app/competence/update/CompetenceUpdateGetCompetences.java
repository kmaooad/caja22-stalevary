package edu.kmaooad.app.competence.update;

import edu.kmaooad.app.activities.Utils;
import edu.kmaooad.app.activities.update.ActivityUpdate;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.ActivityUpdateDto;
import edu.kmaooad.dto.CompetenceUpdateDto;
import edu.kmaooad.model.ActivityEntity;
import edu.kmaooad.service.ActivityService;
import edu.kmaooad.telegram.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.List;

@Component
public class CompetenceUpdateGetCompetences implements Handler {

    private final TelegramService telegramService;
    private final ActivityService activityService;

    public CompetenceUpdateGetCompetences(TelegramService telegramService, ActivityService activityService) {
        this.activityService = activityService;
        this.telegramService = telegramService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
        try {
            String activityId = callbackQuery.getData();

            ActivityUpdateDto payload = new ActivityUpdateDto();
            payload.setActivityId(activityId);


            List<ActivityEntity> entities = activityService.getUserActivities(callbackQuery.getMessage().getChatId().toString());
            entities = entities.stream().filter(a -> a.getId().equals(activityId)).toList();
            ActivityEntity activity = entities.get(0);

            CompetenceUpdateDto competenceUpdateDto = new CompetenceUpdateDto();
            competenceUpdateDto.setActivityUpdateDto(payload);
            competenceUpdateDto.setEntity(activity);

            stateMachine.updateStateData(callbackQuery.getMessage().getChatId(), ActivityUpdate.GROUP, competenceUpdateDto);
            if(activity.getCompetences().size() == 0){
                stateMachine.setState(callbackQuery.getMessage().getChatId(),  new State.Any());
                telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Current activity hasn't competences");
            } else{
                HashMap<String, String> map = new HashMap<>();
                for(String c : activity.getCompetences()){
                    map.put(c, c);
                }
                InlineKeyboardMarkup kb = Utils.getKeyboardFromDict(map);

                stateMachine.setState(callbackQuery.getMessage().getChatId(), CompetenceUpdate.GET_VALUE);
                telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Pick a competence to edit", kb);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());

            telegramService.sendMessage(callbackQuery.getMessage().getChatId(), "Error occurred!");
        }
    }

    @Override
    public State getState() {
        return CompetenceUpdate.GET_COMPETENCES;
    }
}

