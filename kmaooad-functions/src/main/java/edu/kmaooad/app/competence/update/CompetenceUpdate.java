package edu.kmaooad.app.competence.update;

import edu.kmaooad.core.state.State;

public enum CompetenceUpdate implements State, State.Group{
    GROUP, GET_COMPETENCES, GET_VALUE, UPDATE_VALUE;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}
