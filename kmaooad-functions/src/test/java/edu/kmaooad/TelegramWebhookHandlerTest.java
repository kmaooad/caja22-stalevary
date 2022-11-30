package edu.kmaooad;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import edu.kmaooad.dto.BotUpdateResult;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TelegramWebhookHandlerTest {

    private HttpRequestMessage<Optional<String>> setupHttpRequestMock(String body) {
        final HttpRequestMessage<Optional<String>> requestMock = mock(HttpRequestMessage.class);
        when(requestMock.getBody()).thenReturn(Optional.ofNullable(body));

        when(requestMock.createResponseBuilder(any(HttpStatus.class)))
                .thenAnswer((Answer<HttpResponseMessage.Builder>) invocation ->
                        new HttpResponseMessageMock
                                .HttpResponseMessageBuilderMock()
                                .status(invocation.getArgument(0))
                );
        return requestMock;
    }

    private ExecutionContext getExecutionContext() {
        ExecutionContext contextMock = mock(ExecutionContext.class);
        when(contextMock.getLogger()).thenReturn(Logger.getGlobal());
        return contextMock;
    }

    private TelegramWebhookHandler getSucceedHandler() {
        TelegramWebhookHandler handler = spy(TelegramWebhookHandler.class);
        doAnswer(
                invocationOnMock -> {
                    Update update = invocationOnMock.getArgument(0);
                    return BotUpdateResult.Ok(update.getMessage().getMessageId());
                }
        )
                .when(handler)
                .handleRequest(any(Update.class), any(ExecutionContext.class));
        return handler;
    }

    private TelegramWebhookHandler getFailedHandler() {
        TelegramWebhookHandler handler = spy(TelegramWebhookHandler.class);
        doAnswer(
                invocationOnMock -> BotUpdateResult.Error("")
        )
                .when(handler)
                .handleRequest(any(Update.class), any(ExecutionContext.class));
        return handler;
    }

    @Test
    public void shouldReturnBadResponseWhenEmptyBody() {

        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock(null);
        final HttpResponseMessage response = getSucceedHandler().run(request, getExecutionContext());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    public void shouldReturnBadResponseWhenIncorrectJsonBody() {

        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("string");
        final HttpResponseMessage response = getSucceedHandler().run(request, getExecutionContext());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    public void shouldReturnBadResponseWhenMessageNonPresent() {

        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("""
                {
                   "msg":{
                     \s
                   }
                }""");
        final HttpResponseMessage response = getSucceedHandler().run(request, getExecutionContext());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    public void shouldReturnOkResponse() {

        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("""
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
                }""");
        final HttpResponseMessage response = getSucceedHandler().run(request, getExecutionContext());

        assertEquals(207, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void shouldReturnBadResponseForFailedHandler() {

        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("""
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
                }""");
        final HttpResponseMessage response = getFailedHandler().run(request, getExecutionContext());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }
}
