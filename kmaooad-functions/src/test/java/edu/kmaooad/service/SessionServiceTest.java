package edu.kmaooad.service;

import edu.kmaooad.core.session.UserSession;
import edu.kmaooad.core.state.State;
import edu.kmaooad.exception.IncorrectIdException;
import edu.kmaooad.model.SessionEntity;
import edu.kmaooad.repository.SessionRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class SessionServiceTest {

    private final SessionRepository sessionRepository = mock(SessionRepository.class);

    private final SessionService sessionService = new SessionService(sessionRepository);

    @Test
    void shouldGetSessionById() {
        SessionEntity session = new SessionEntity(1L, "state", new HashMap<>());
        when(sessionRepository.findById(any()))
                .thenReturn(Optional.of(session));

        Optional<UserSession> result = sessionService.getSessionByUserId(1L);

        assertTrue(result.isPresent());
        verify(sessionRepository, times(1)).findById(any());
        assertEquals(result.get().id(), session.getUserId());
        assertEquals(result.get().currentState(), session.getState());
        assertEquals(result.get().statesPayload(), session.getPayload());
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoSession() {
        when(sessionRepository.findById(any()))
                .thenReturn(Optional.empty());

        Optional<UserSession> result = sessionService.getSessionByUserId(1L);

        assertTrue(result.isEmpty());
        verify(sessionRepository, times(1)).findById(any());
    }

    @Test
    void shouldCreateEmptySession() throws IncorrectIdException {
        SessionEntity session = new SessionEntity(1L, "state", new HashMap<>());

        when(sessionRepository.findById(any()))
                .thenReturn(Optional.empty());
        when(sessionRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        UserSession result = sessionService.createSessionByUserId(1L);

        verify(sessionRepository, times(1)).findById(any());
        verify(sessionRepository, times(1)).save(any());

        assertEquals(result.id(), session.getUserId());
        assertEquals(result.currentState(), new State.Any().key());
        assertEquals(result.statesPayload(), new HashMap<>());
    }

    @Test
    void shouldThrowExceptionForExistingSession() {
        when(sessionRepository.findById(any()))
                .thenReturn(Optional.of(new SessionEntity(1L, "state", new HashMap<>())));
        when(sessionRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThrows(IncorrectIdException.class, () -> sessionService.createSessionByUserId(1L));

        verify(sessionRepository, times(1)).findById(any());
        verify(sessionRepository, times(0)).save(any());
    }

    @Test
    void shouldClearExistingSession() throws IncorrectIdException {
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("key", "value");
        SessionEntity session = new SessionEntity(1L, "state", payloadMap);

        when(sessionRepository.findById(any()))
                .thenReturn(Optional.of(session));
        when(sessionRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        UserSession result = sessionService.clearSessionByUserId(1L);

        verify(sessionRepository, times(1)).findById(any());
        verify(sessionRepository, times(1)).save(any());

        assertEquals(session.getUserId(), result.id());
        assertEquals(new State.Any().key(), result.currentState());
        assertEquals(new HashMap<>(), result.statesPayload());
    }

    @Test
    void shouldThrowExceptionOnClearNoSession() {
        when(sessionRepository.findById(any()))
                .thenReturn(Optional.empty());
        when(sessionRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThrows(IncorrectIdException.class, () -> sessionService.clearSessionByUserId(1L));

        verify(sessionRepository, times(1)).findById(any());
        verify(sessionRepository, times(0)).save(any());
    }

    @Test
    void shouldUpdateSessionState() throws IncorrectIdException {
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("key", "value");
        SessionEntity session = new SessionEntity(1L, "state", payloadMap);

        when(sessionRepository.findById(any()))
                .thenReturn(Optional.of(session));
        when(sessionRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        UserSession result = sessionService.updateSessionState(1L, new State.Any());

        verify(sessionRepository, times(1)).findById(any());
        verify(sessionRepository, times(1)).save(any());

        assertEquals(session.getUserId(), result.id());
        assertEquals(new State.Any().key(), result.currentState());
        assertEquals(session.getPayload(), result.statesPayload());
    }

    @Test
    void shouldThrowExceptionOnUpdateSessionState() {
        when(sessionRepository.findById(any()))
                .thenReturn(Optional.empty());
        when(sessionRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThrows(IncorrectIdException.class, () -> sessionService.updateSessionState(1L, new State.Any()));

        verify(sessionRepository, times(1)).findById(any());
        verify(sessionRepository, times(0)).save(any());
    }

    @Test
    void shouldUpdateSessionPayloadForExistingKey() throws IncorrectIdException {
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("key", "value");
        SessionEntity session = new SessionEntity(1L, "state", payloadMap);

        when(sessionRepository.findById(any()))
                .thenReturn(Optional.of(session));
        when(sessionRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        UserSession result = sessionService.updateGroupStatePayload(1L, group, "new_state");

        verify(sessionRepository, times(1)).findById(any());
        verify(sessionRepository, times(1)).save(any());

        assertEquals(session.getUserId(), result.id());
        assertEquals(session.getState(), result.currentState());
        assertEquals("new_state", result.statesPayload().get("key"));
    }

    @Test
    void shouldUpdateSessionPayloadForNonExistingKey() throws IncorrectIdException {
        SessionEntity session = new SessionEntity(1L, "state", new HashMap<>());

        when(sessionRepository.findById(any()))
                .thenReturn(Optional.of(session));
        when(sessionRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        UserSession result = sessionService.updateGroupStatePayload(1L, group, "new_state");

        verify(sessionRepository, times(1)).findById(any());
        verify(sessionRepository, times(1)).save(any());

        assertEquals(session.getUserId(), result.id());
        assertEquals(session.getState(), result.currentState());
        assertEquals("new_state", result.statesPayload().get("key"));
    }

    @Test
    void shouldThrowExceptionOnUpdateSessionPayload() {
        when(sessionRepository.findById(any()))
                .thenReturn(Optional.empty());
        when(sessionRepository.save(any()))
                .then(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThrows(IncorrectIdException.class, () -> sessionService.updateGroupStatePayload(1L, group, "new_state"));

        verify(sessionRepository, times(1)).findById(any());
        verify(sessionRepository, times(0)).save(any());
    }

    @Test
    void shouldReturnNullSessionPayload() throws IncorrectIdException {
        SessionEntity session = new SessionEntity(1L, "state", new HashMap<>());

        when(sessionRepository.findById(any()))
                .thenReturn(Optional.of(session));

        String result = sessionService.getGroupStatePayload(1L, group);

        verify(sessionRepository, times(1)).findById(any());
        assertNull(result);
    }

    @Test
    void shouldReturnSessionPayload() throws IncorrectIdException {
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("key", "value");
        SessionEntity session = new SessionEntity(1L, "state", payloadMap);

        when(sessionRepository.findById(any()))
                .thenReturn(Optional.of(session));

        String result = sessionService.getGroupStatePayload(1L, group);

        verify(sessionRepository, times(1)).findById(any());
        assertEquals("value", result);
    }

    @Test
    void shouldThrowExceptionOnGetSessionPayload() {
        when(sessionRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(IncorrectIdException.class, () -> sessionService.getGroupStatePayload(1L, group));

        verify(sessionRepository, times(1)).findById(any());
    }

    private final State.Group group = new State.Group() {
        @Override
        public String group() {
            return "key";
        }
    };
}
