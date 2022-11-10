package edu.kmaooad;

import edu.kmaooad.dto.BotUpdate;
import edu.kmaooad.dto.BotUpdateResult;
import edu.kmaooad.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TelegramWebhook implements Function<BotUpdate, BotUpdateResult> {

    private final MessageService messageService;

    public TelegramWebhook(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public BotUpdateResult apply(BotUpdate botUpdate) {

        if (botUpdate.getErrorMessage() != null)
            return BotUpdateResult.Error(botUpdate.getErrorMessage());

        try {
            messageService.saveMessage(botUpdate);
            return BotUpdateResult.Ok(botUpdate.getMessageId());
        } catch (Exception exception) {
            return BotUpdateResult.Error(exception.getMessage());
        }
    }
}
