package edu.kmaooad;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import edu.kmaooad.dto.BotUpdate;
import edu.kmaooad.service.MessageService;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TelegramWebhookTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public MessageService getMessageService() {
            MessageService service = mock(MessageService.class);
            return service;
        }
    }

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

    private TelegramWebhookHandler getHandler() {
        TelegramWebhookHandler handler = spy(TelegramWebhookHandler.class);
        doAnswer(
                invocationOnMock -> applicationContext
                        .getBean(TelegramWebhook.class)
                        .apply(invocationOnMock.getArgument(0))
        )
                .when(handler)
                .handleRequest(any(BotUpdate.class), any(ExecutionContext.class));
        return handler;
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void shouldReturnBadResponseWhenEmptyBody() {

        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock(null);
        final HttpResponseMessage response = getHandler().run(request, getExecutionContext());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    public void shouldReturnBadResponseWhenIncorrectJsonBody() {

        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("string");
        final HttpResponseMessage response = getHandler().run(request, getExecutionContext());

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
        final HttpResponseMessage response = getHandler().run(request, getExecutionContext());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    public void shouldReturnBadResponseWhenMessageIdNonPresent() {

        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("""
                {
                   "message":{
                     \s
                   }
                }""");
        final HttpResponseMessage response = getHandler().run(request, getExecutionContext());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    public void shouldReturnOkResponse() {

        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("""
                {
                   "message":{
                      "message_id": "1"
                   }
                }""");
        final HttpResponseMessage response = getHandler().run(request, getExecutionContext());

        assertEquals("1", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}
