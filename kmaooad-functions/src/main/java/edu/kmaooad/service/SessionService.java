package edu.kmaooad.service;

import edu.kmaooad.core.session.ISessionService;
import edu.kmaooad.core.session.UserSession;
import edu.kmaooad.core.state.State;
import edu.kmaooad.model.SessionEntity;
import edu.kmaooad.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SessionService implements ISessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Optional<UserSession> getSessionByUserId(Long userId) {
        Optional<SessionEntity> session = sessionRepository
                .findById(userId);

        if (session.isPresent()) {

            SessionEntity sessionEntity = session.get();
            UserSession userSession = new UserSession(
                    sessionEntity.getUserId(),
                    sessionEntity.getState(),
                    sessionEntity.getPayload());
            return Optional.of(userSession);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public UserSession createSessionByUserId(Long userId) {
        Map<String, String> map = new HashMap<>();
        SessionEntity sessionEntity = sessionRepository.save(new SessionEntity(userId, new State.Any().key(), map));
        return new UserSession(sessionEntity.getUserId(), sessionEntity.getState(), sessionEntity.getPayload());
    }

    //TODO: catch exception
    @Override
    public void clearSessionByUserId(Long userId) {
        SessionEntity session = sessionRepository
                .findById(userId).get();

        session.setPayload(new HashMap<>());
        sessionRepository.save(session);
    }

    //TODO: catch exception
    @Override
    public void updateSessionState(Long userId, State state) {
        SessionEntity session = sessionRepository
                .findById(userId).get();

        session.setState(state.key());
        sessionRepository.save(session);
    }

    @Override
    public void updateGroupStatePayload(Long userId, State.Group group, String payload) {
        SessionEntity session = sessionRepository
                .findById(userId).get();

        Map<String, String> payloadMap = session.getPayload();
        payloadMap.put(group.group(), payload);
        session.setPayload(payloadMap);

        sessionRepository.save(session);
    }

    @Override
    public String getGroupStatePayload(Long userId, State.Group group) {
        SessionEntity session = sessionRepository
                .findById(userId).get();

        Map<String, String> payloadMap = session.getPayload();
        return payloadMap.get(group.group());
    }
}
