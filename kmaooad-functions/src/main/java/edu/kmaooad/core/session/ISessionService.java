package edu.kmaooad.core.session;

import edu.kmaooad.core.state.State;

import java.util.Optional;

public interface ISessionService {

    Optional<UserSession> getSessionByUserId(Long userId);

    UserSession createSessionByUserId(Long userId);

    void clearSessionByUserId(Long userId);

    void updateSessionState(Long userId, State state);


    void updateGroupStatePayload(Long userId, State.Group group, String payload);

    String getGroupStatePayload(Long userId, State.Group group);
}
