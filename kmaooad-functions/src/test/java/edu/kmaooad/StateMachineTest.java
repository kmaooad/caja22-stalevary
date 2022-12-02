package edu.kmaooad;

import edu.kmaooad.app.StateMachineImpl;
import edu.kmaooad.core.StateMachineException;
import edu.kmaooad.core.session.ISessionService;
import edu.kmaooad.core.session.UserSession;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.dto.CourseDto;
import edu.kmaooad.exception.IncorrectIdException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StateMachineTest {

    private final ISessionService sessionService = mock(ISessionService.class);

    private final StateMachine stateMachine = new StateMachineImpl(sessionService);

    @Test
    void shouldThrowExceptionWhenUpdateStateAndUserNotExist() throws IncorrectIdException {
        when(sessionService.updateSessionState(any(), any()))
                .thenThrow(IncorrectIdException.class);

        assertThrows(StateMachineException.class, () -> stateMachine.updateStateData(1L, group, new Object()));
    }

    @Test
    void shouldThrowExceptionWhenUpdateSessionWithNonSerializableObject() throws IncorrectIdException {
        when(sessionService.updateSessionState(any(), any()))
                .thenReturn(new UserSession(1L, "state", new HashMap<>()));

        assertThrows(StateMachineException.class, () -> stateMachine.updateStateData(1L, group, new Object()));
    }

    @Test
    void shouldUpdateSession() throws StateMachineException, IncorrectIdException {
        stateMachine.updateStateData(1L, group, new CourseDto());

        verify(sessionService, times(1))
                .updateGroupStatePayload(1L, group, "{\"title\":null,\"description\":null}");
    }


    @Test
    void shouldThrowExceptionWhenSetStateAndUserNotExist() throws IncorrectIdException {
        when(sessionService.updateSessionState(any(), any()))
                .thenThrow(IncorrectIdException.class);

        assertThrows(StateMachineException.class, () -> stateMachine.setState(1L, new State.Any()));
    }

    @Test
    void shouldSetState() throws StateMachineException, IncorrectIdException {
        stateMachine.setState(1L, new State.Any());

        verify(sessionService, times(1))
                .updateSessionState(1L, new State.Any());
    }


    @Test
    void shouldReturnEmptyOptionalWhenGetStatePayloadAndUserNotExist() throws IncorrectIdException {
        when(sessionService.getGroupStatePayload(any(), any()))
                .thenThrow(IncorrectIdException.class);

        Optional<Integer> result = stateMachine.getStatePayload(1L, group, Integer.class);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenGetStatePayloadAndPayloadNotExist() throws IncorrectIdException {
        when(sessionService.getGroupStatePayload(any(), any()))
                .thenReturn(null);

        Optional<Integer> result = stateMachine.getStatePayload(1L, group, Integer.class);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldGetStatePayload() throws IncorrectIdException {
        when(sessionService.getGroupStatePayload(any(), any()))
                .thenReturn("{\"title\":\"TITLE\",\"description\":\"DESC\"}");

        Optional<CourseDto> result = stateMachine.getStatePayload(1L, group, CourseDto.class);
        assertTrue(result.isPresent());
    }


    private final State.Group group = new State.Group() {
        @Override
        public String group() {
            return "key";
        }
    };
}
