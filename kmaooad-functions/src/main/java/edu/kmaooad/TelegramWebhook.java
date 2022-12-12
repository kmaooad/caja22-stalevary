package edu.kmaooad;

import edu.kmaooad.core.Dispatcher;
import edu.kmaooad.core.Handler;
import edu.kmaooad.dto.BotUpdateResult;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppData;

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
        if (update == null) {
            return BotUpdateResult.Error(emptyUpdateMessage());
        }

        if (update.hasMessage()) {
            Message message = update.getMessage();
            dispatcher.dispatch(message);
            return BotUpdateResult.Ok(message.getMessageId());
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            dispatcher.dispatch(callbackQuery);
            return BotUpdateResult.Ok(callbackQuery.getMessage().getMessageId());
        }

        return BotUpdateResult.Error(emptyUpdateMessage());
    }

    static String emptyUpdateMessage() {
        return "Empty update";
    }
}
