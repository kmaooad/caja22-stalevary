package edu.kmaooad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.core.Dispatcher;
import edu.kmaooad.core.Handler;
import edu.kmaooad.core.session.ISessionService;
import edu.kmaooad.core.session.UserSession;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.exception.IncorrectIdException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static com.mongodb.assertions.Assertions.assertFalse;
import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.mockito.Mockito.*;

public class DispatcherTest {

    private final StateMachine stateMachine = mock(StateMachine.class);
    private final ISessionService sessionService = mock(ISessionService.class);

    private Dispatcher dispatcher() {
        return new Dispatcher(stateMachine, sessionService);
    }

    @Test
    void shouldCreateUserAndDispatch() throws IncorrectIdException {
        Handler commandHandler = mock(Handler.class);
        when(commandHandler.getCommands())
                .thenReturn(Collections.singletonList("course"));

        Handler stateHandler = mock(Handler.class);
        when(stateHandler.getState())
                .thenReturn(new State.Any());

        Handler filterHandler = mock(Handler.class);
        when(filterHandler.getFilter())
                .thenReturn(message -> true);

        when(sessionService.getSessionByUserId(any()))
                .thenReturn(Optional.empty());
        when(sessionService.createSessionByUserId(any()))
                .thenReturn(new UserSession(1L, new State.Any().key(), new HashMap<>()));

        Dispatcher dispatcher = dispatcher();

        dispatcher.registerHandler(commandHandler);
        dispatcher.registerHandler(stateHandler);
        dispatcher.registerHandler(filterHandler);

        boolean handled = dispatcher.dispatch(Objects.requireNonNull(update()).getMessage());

        assertTrue(handled);
        verify(commandHandler, times(1)).handle((Message) any(), any());
        verify(stateHandler, times(0)).handle((Message) any(), any());
        verify(filterHandler, times(0)).handle((Message) any(), any());
    }

    @Test
    void shouldDispatchCommandHandler() {
        Handler commandHandler = mock(Handler.class);
        when(commandHandler.getCommands())
                .thenReturn(Collections.singletonList("course"));

        Handler stateHandle = mock(Handler.class);
        when(stateHandle.getState())
                .thenReturn(new State.Any());

        Handler filterHandler = mock(Handler.class);
        when(filterHandler.getFilter())
                .thenReturn(message -> true);

        when(sessionService.getSessionByUserId(any()))
                .thenReturn(Optional.of(new UserSession(1L, new State.Any().key(), new HashMap<>())));

        Dispatcher dispatcher = dispatcher();
        dispatcher.registerHandler(commandHandler);
        dispatcher.registerHandler(stateHandle);
        dispatcher.registerHandler(filterHandler);

        boolean handled = dispatcher.dispatch(Objects.requireNonNull(update()).getMessage());

        assertTrue(handled);
        verify(commandHandler, times(1)).handle((Message) any(), any());
        verify(stateHandle, times(0)).handle((Message) any(), any());
        verify(filterHandler, times(0)).handle((Message) any(), any());
    }

    @Test
    void shouldDispatchStateHandler() {

        Handler stateHandle = mock(Handler.class);
        when(stateHandle.getState())
                .thenReturn(new State.Any());

        Handler filterHandler = mock(Handler.class);
        when(filterHandler.getFilter())
                .thenReturn(message -> true);

        when(sessionService.getSessionByUserId(any()))
                .thenReturn(Optional.of(new UserSession(1L, new State.Any().key(), new HashMap<>())));

        Dispatcher dispatcher = dispatcher();
        dispatcher.registerHandler(stateHandle);
        dispatcher.registerHandler(filterHandler);
        boolean handled = dispatcher.dispatch(Objects.requireNonNull(update()).getMessage());

        assertTrue(handled);
        verify(stateHandle, times(1)).handle((Message) any(), any());
        verify(filterHandler, times(0)).handle((Message) any(), any());
    }

    @Test
    void shouldDispatchFilterHandler() {

        Handler filterHandler = mock(Handler.class);

        when(filterHandler.getFilter())
                .thenReturn(message -> true);
        when(sessionService.getSessionByUserId(any()))
                .thenReturn(Optional.of(new UserSession(1L, new State.Any().key(), new HashMap<>())));

        Dispatcher dispatcher = dispatcher();
        dispatcher.registerHandler(filterHandler);
        boolean handled = dispatcher.dispatch(Objects.requireNonNull(update()).getMessage());

        assertTrue(handled);
        verify(filterHandler, times(1)).handle((Message) any(), any());
    }

    @Test
    void shouldReturnNonDispatched() {
        when(sessionService.getSessionByUserId(any()))
                .thenReturn(Optional.of(new UserSession(1L, new State.Any().key(), new HashMap<>())));

        Dispatcher dispatcher = dispatcher();
        boolean handled = dispatcher.dispatch(Objects.requireNonNull(update()).getMessage());

        assertFalse(handled);
    }

    private Update update() {
        String updateJson = """
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
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(updateJson, Update.class);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }
}
