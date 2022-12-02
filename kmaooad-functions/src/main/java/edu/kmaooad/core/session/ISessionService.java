package edu.kmaooad.core.session;

import edu.kmaooad.core.state.State;
import edu.kmaooad.exception.IncorrectIdException;

import java.util.Optional;

public interface ISessionService {

    Optional<UserSession> getSessionByUserId(Long userId);

    UserSession createSessionByUserId(Long userId) throws IncorrectIdException;

    UserSession clearSessionByUserId(Long userId) throws IncorrectIdException;

    UserSession updateSessionState(Long userId, State state) throws IncorrectIdException;

    UserSession updateGroupStatePayload(Long userId, State.Group group, String payload) throws IncorrectIdException;

    String getGroupStatePayload(Long userId, State.Group group) throws IncorrectIdException;
}
