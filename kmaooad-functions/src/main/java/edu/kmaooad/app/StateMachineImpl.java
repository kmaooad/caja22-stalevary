package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.core.session.ISessionService;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.exception.IncorrectIdException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StateMachineImpl implements StateMachine {

    private final ISessionService sessionService;

    public StateMachineImpl(ISessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void updateStateData(Long userId, State.Group group, Object obj) {
        try {
            String payloadString = new ObjectMapper().writeValueAsString(obj);

            sessionService.updateGroupStatePayload(userId, group, payloadString);
        } catch (JsonProcessingException | IncorrectIdException exception) {
        }
    }

    @Override
    public <PAYLOAD> Optional<PAYLOAD> getStatePayload(Long userId, State.Group group, Class<PAYLOAD> payloadClass) {
        try {
            String payload = sessionService.getGroupStatePayload(userId, group);
            ObjectMapper mapper = new ObjectMapper();

            return Optional.of(mapper.readValue(payload, payloadClass));
        } catch (JsonProcessingException | IncorrectIdException exception) {
            return Optional.empty();
        }
    }

    @Override
    public void setState(Long userId, State state) {
        try {
            sessionService.updateSessionState(userId, state);
        } catch (IncorrectIdException e) {
        }
    }
}
