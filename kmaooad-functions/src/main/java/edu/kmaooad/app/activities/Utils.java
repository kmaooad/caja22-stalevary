package edu.kmaooad.app.activities;

import edu.kmaooad.model.ActivityEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public class Utils {
    public static InlineKeyboardMarkup getActivitiesKeyboard(List<ActivityEntity> entities) {
        List<List<InlineKeyboardButton>> rows = entities.stream().map(entity -> {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(entity.getTitle() + " " + entity.getDate() + " " + entity.getTime());
            btn.setCallbackData(entity.getId());
            return List.of(btn);
        }).toList();

        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup getKeyboardFromDict(HashMap<String, String> map) {
        List<List<InlineKeyboardButton>> rows = map.entrySet().stream().map(entry -> {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(entry.getKey());
            btn.setCallbackData(entry.getValue());
            return List.of(btn);
        }).toList();

        return new InlineKeyboardMarkup(rows);
    }
}
