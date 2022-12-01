package edu.kmaooad.service;

import edu.kmaooad.core.session.ISessionService;
import edu.kmaooad.core.session.UserSession;
import edu.kmaooad.core.state.State;
import edu.kmaooad.exception.IncorrectIdException;
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
    public UserSession createSessionByUserId(Long userId) throws IncorrectIdException {
        Optional<SessionEntity> session = sessionRepository.findById(userId);

        if (session.isPresent()) throw new IncorrectIdException();

        Map<String, String> map = new HashMap<>();
        SessionEntity sessionEntity = sessionRepository.save(new SessionEntity(userId, new State.Any().key(), map));
        return new UserSession(sessionEntity.getUserId(), sessionEntity.getState(), sessionEntity.getPayload());
    }

    @Override
    public UserSession clearSessionByUserId(Long userId) throws IncorrectIdException {
        SessionEntity session = sessionRepository
                .findById(userId)
                .orElseThrow(IncorrectIdException::new);

        session.setState(new State.Any().key());
        session.setPayload(new HashMap<>());
        SessionEntity clearedSession = sessionRepository.save(session);
        return new UserSession(clearedSession.getUserId(), clearedSession.getState(), clearedSession.getPayload());
    }

    @Override
    public UserSession updateSessionState(Long userId, State state) throws IncorrectIdException {
        SessionEntity session = sessionRepository
                .findById(userId)
                .orElseThrow(IncorrectIdException::new);

        session.setState(state.key());
        SessionEntity clearedSession = sessionRepository.save(session);
        return new UserSession(clearedSession.getUserId(), clearedSession.getState(), clearedSession.getPayload());
    }

    @Override
    public UserSession updateGroupStatePayload(Long userId, State.Group group, String payload) throws IncorrectIdException {
        SessionEntity session = sessionRepository
                .findById(userId)
                .orElseThrow(IncorrectIdException::new);

        Map<String, String> payloadMap = session.getPayload();
        payloadMap.put(group.group(), payload);
        session.setPayload(payloadMap);

        SessionEntity clearedSession = sessionRepository.save(session);
        return new UserSession(clearedSession.getUserId(), clearedSession.getState(), clearedSession.getPayload());
    }

    @Override
    public String getGroupStatePayload(Long userId, State.Group group) throws IncorrectIdException {
        SessionEntity session = sessionRepository
                .findById(userId)
                .orElseThrow(IncorrectIdException::new);

        Map<String, String> payloadMap = session.getPayload();
        return payloadMap.get(group.group());
    }
}
