package edu.kmaooad.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.springframework.beans.factory.annotation.Value;

@Component
public class TelegramApi extends DefaultAbsSender {

    @Value("${bot.token}")
    private String botToken;

    protected TelegramApi() {
        super(new DefaultBotOptions());
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
