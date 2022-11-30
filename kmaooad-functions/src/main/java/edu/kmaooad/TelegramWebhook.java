package edu.kmaooad;

import edu.kmaooad.core.Dispatcher;
import edu.kmaooad.core.Handler;
import edu.kmaooad.dto.BotUpdateResult;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.function.Function;

@Component
public class TelegramWebhook implements Function<Update, BotUpdateResult> {

    private final Dispatcher dispatcher;

    public TelegramWebhook(Dispatcher dispatcher, List<Handler> handlers) {

        this.dispatcher = dispatcher;
        handlers.forEach(dispatcher::registerHandler);
    }

    @Override
    public BotUpdateResult apply(Update update) {
        if (update != null && update.hasMessage()) {
            Message message = update.getMessage();
            dispatcher.dispatch(message);
            return BotUpdateResult.Ok(message.getMessageId());
        }
        else
            return BotUpdateResult.Error("Empty update");
    }
}
