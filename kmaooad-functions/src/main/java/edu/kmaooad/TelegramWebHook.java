package edu.kmaooad;

import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TelegramWebHook implements Function<BotUpdate, BotUpdateResult> {

    @Override
    public BotUpdateResult apply(BotUpdate botUpdate) {
        if (botUpdate.getErrorMessage() == null) {
            return BotUpdateResult.Ok(botUpdate.getMessageId());
        } else {
            return BotUpdateResult.Error(botUpdate.getErrorMessage());
        }
    }
}
