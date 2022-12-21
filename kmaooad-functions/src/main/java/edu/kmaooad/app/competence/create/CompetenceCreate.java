package edu.kmaooad.app.competence.create;

import edu.kmaooad.core.state.State;

public enum CompetenceCreate implements State, State.Group {
    GROUP, GET_ACTIVITY_ID, GET_COMPETENCES;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}
