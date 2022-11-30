package edu.kmaooad.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.core.session.ISessionService;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
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
            System.out.println("SESSION_PAYLOAD_UPDATE: " + payloadString);

            sessionService.updateGroupStatePayload(userId, group, payloadString);
        } catch (JsonProcessingException exception) {
            System.out.println("SESSION_PAYLOAD_UPDATE_EXCEPTION: " + exception);
        }
    }

    @Override
    public <PAYLOAD> Optional<PAYLOAD> getStatePayload(Long userId, State.Group group, Class<PAYLOAD> payloadClass) {
        String payload = sessionService.getGroupStatePayload(userId, group);

        System.out.println("SESSION_PAYLOAD_GET: " + payload);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return Optional.of(mapper.readValue(payload, payloadClass));
        } catch (JsonProcessingException exception) {
            System.out.println("SESSION_PAYLOAD_GET_EXCEPTION: " + exception);
            return Optional.empty();
        }
    }

    @Override
    public void setState(Long userId, State state) {
        sessionService.updateSessionState(userId, state);
    }
}
