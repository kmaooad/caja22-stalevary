package edu.kmaooad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.core.Dispatcher;
import edu.kmaooad.core.Handler;
import edu.kmaooad.dto.BotUpdateResult;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TelegramWebhookTest {

    @Test
    void shouldReturnBadUpdateForNullParam() {
        Dispatcher dispatcher = mock(Dispatcher.class);

        List<Handler> handlerList = new ArrayList<>();
        handlerList.add(mock(Handler.class));
        handlerList.add(mock(Handler.class));
        handlerList.add(mock(Handler.class));

        BotUpdateResult result = new TelegramWebhook(dispatcher, handlerList).apply(null);

        verify(dispatcher, times(0)).dispatch((Message) any());
        assertEquals(result.errorMessage(), TelegramWebhook.emptyUpdateMessage());
    }

    @Test
    void shouldReturnBadUpdateForEmptyMessage() {
        Dispatcher dispatcher = mock(Dispatcher.class);

        List<Handler> handlerList = new ArrayList<>();
        handlerList.add(mock(Handler.class));
        handlerList.add(mock(Handler.class));
        handlerList.add(mock(Handler.class));

        BotUpdateResult result = new TelegramWebhook(dispatcher, handlerList).apply(updateWithoutMessage());

        verify(dispatcher, times(0)).dispatch((Message) any());
        assertEquals(result.errorMessage(), TelegramWebhook.emptyUpdateMessage());
    }

    @Test
    void shouldReturnOkUpdate() {
        Dispatcher dispatcher = mock(Dispatcher.class);

        List<Handler> handlerList = new ArrayList<>();
        handlerList.add(mock(Handler.class));
        handlerList.add(mock(Handler.class));
        handlerList.add(mock(Handler.class));

        Update update = updateWithMessage();

        BotUpdateResult result = new TelegramWebhook(dispatcher, handlerList).apply(update);

        verify(dispatcher, times(1)).dispatch((Message) any());
        assertEquals(update.getMessage().getMessageId(), result.messageId());
    }

    private Update updateWithoutMessage() {
        String updateJson = """
                {
                   "update_id":260336395
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(updateJson, Update.class);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }

    private Update updateWithMessage() {
        String updateJson = """
                {
                   "update_id":260336395,
                   "message":{
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
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(updateJson, Update.class);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }
}
