package edu.kmaooad.core.state;

import edu.kmaooad.core.StateMachineException;

import java.util.Optional;

public interface StateMachine {

    void updateStateData(Long userId, State.Group group, Object obj) throws StateMachineException;

    <PAYLOAD> Optional<PAYLOAD> getStatePayload(Long userId, State.Group group, Class<PAYLOAD> payloadClass);

    void setState(Long userId, State state) throws StateMachineException;
}
