package edu.kmaooad.service;

import edu.kmaooad.telegram.TelegramApi;
import edu.kmaooad.telegram.TelegramService;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TelegramServiceTest {

    private final TelegramApi telegramApi = mock(TelegramApi.class);

    private final TelegramService telegramService = new TelegramService(telegramApi);

    @Test
    void shouldExecuteOnMessageSend() throws TelegramApiException {
        telegramService.sendMessage(1L, "text");
        verify(telegramApi, times(1)).execute(any(SendMessage.class));
    }

    @Test
    void shouldThrowExceptionOnDownloadFile() throws TelegramApiException {
        when(telegramApi.getBotToken())
                .thenReturn("token");
        assertThrows(RuntimeException.class, () -> telegramService.downloadFile(new File(), "text"));
        verify(telegramApi, times(0)).execute(any(SendMessage.class));
    }

    @Test
    void shouldExecuteOnMessageWithKeyboardSend() throws TelegramApiException {
        telegramService.sendMessage(1L, "text", new ReplyKeyboardMarkup());
        verify(telegramApi, times(1)).execute(any(SendMessage.class));
    }

    @Test
    void shouldExecuteOnGetFile() throws TelegramApiException {
        telegramService.getFile(new GetFile());
        verify(telegramApi, times(1)).execute(any(GetFile.class));
    }
}
