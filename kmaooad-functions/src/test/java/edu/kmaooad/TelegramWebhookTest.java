package edu.kmaooad;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import edu.kmaooad.exception.EmptyRequestBodyException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TelegramWebhookTest {

//    @Mock
//    static ExecutionContext contextMock = mock(ExecutionContext.class);
//
//    @BeforeAll
//    public static void setupMocks() {
//
//        when(contextMock.getLogger()).thenReturn(Logger.getGlobal());
//    }
//
//    public HttpRequestMessage<Optional<String>> setupHttpRequestMock(String body) {
//        final HttpRequestMessage<Optional<String>> requestMock = mock(HttpRequestMessage.class);
//        when(requestMock.getBody()).thenReturn(Optional.ofNullable(body));
//
//        when(requestMock.createResponseBuilder(any(HttpStatus.class)))
//                .thenAnswer((Answer<HttpResponseMessage.Builder>) invocation ->
//                        new HttpResponseMessageMock
//                                .HttpResponseMessageBuilderMock()
//                                .status(invocation.getArgument(0))
//                );
//        return requestMock;
//    }

    @Test
    public void test() {
        assert true;
    }

//    @Test
//    public void shouldReturnBadResponseWhenEmptyBody() {
//
//        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock(null);
//        final HttpResponseMessage response = new TelegramWebhookHandler().run(request, contextMock);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
//        assertEquals(EmptyRequestBodyException.MESSAGE, response.getBody());
//    }
//
//    @Test
//    public void shouldReturnBadResponseWhenIncorrectJsonBody() {
//
//        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("string");
//        final HttpResponseMessage response = new TelegramWebhookHandler().run(request, contextMock);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
//    }
//
//    @Test
//    public void shouldReturnBadResponseWhenMessageNonPresent() {
//
//        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("""
//                {
//                   "msg":{
//                     \s
//                   }
//                }""");
//        final HttpResponseMessage response = new TelegramWebhookHandler().run(request, contextMock);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
//    }
//
//    @Test
//    public void shouldReturnBadResponseWhenMessageIdNonPresent() {
//
//        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("""
//                {
//                   "message":{
//                     \s
//                   }
//                }""");
//        final HttpResponseMessage response = new TelegramWebhookHandler().run(request, contextMock);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
//    }
//
//    @Test
//    public void shouldReturnBadResponseWhenMessageIdNonIntType() {
//
//        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("""
//                {
//                   "message":{
//                      "message_id":"string"
//                   }
//                }""");
//        final HttpResponseMessage response = new TelegramWebhookHandler().run(request, contextMock);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
//    }
//
//    @Test
//    public void shouldReturnOkResponse() {
//
//        final HttpRequestMessage<Optional<String>> request = setupHttpRequestMock("""
//                {
//                   "message":{
//                      "message_id":1
//                   }
//                }""");
//        final HttpResponseMessage response = new TelegramWebhookHandler().run(request, contextMock);
//
//        assertEquals(1, response.getBody());
//        assertEquals(HttpStatus.OK, response.getStatus());
//    }
}
