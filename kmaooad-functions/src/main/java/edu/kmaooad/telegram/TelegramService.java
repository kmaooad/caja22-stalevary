package edu.kmaooad.telegram;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class TelegramService {

    private final TelegramApi telegramApi;

    public TelegramService(TelegramApi telegramApi) {
        this.telegramApi = telegramApi;
    }

    public void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = SendMessage
                .builder()
                .text(text)
                .chatId(chatId.toString())
                //Other possible parse modes: MARKDOWNV2, MARKDOWN, which allows to make text bold, and all other things
                .parseMode(ParseMode.HTML)
                .replyMarkup(replyKeyboard)
                .build();
        execute(sendMessage);
    }

    public File getFile(GetFile getFile) {
        try {
            return telegramApi.execute(getFile);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public java.io.File downloadFile(File file, String fileName) {
        java.io.File localFile = new java.io.File("src/main/resources/files/" + fileName);

        try (InputStream is = new URL(file.getFileUrl(telegramApi.getBotToken()))
                .openStream()) {
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return localFile;
    }

    private void execute(BotApiMethod<Message> botApiMethod) {
        try {
            telegramApi.execute(botApiMethod);
        } catch (Exception e) {
            //TODO: catch API exception
        }
    }
}
