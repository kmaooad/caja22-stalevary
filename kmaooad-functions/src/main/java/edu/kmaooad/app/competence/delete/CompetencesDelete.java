package edu.kmaooad.app.competence.delete;

import edu.kmaooad.core.state.State;

public enum CompetencesDelete implements State, State.Group{
    GROUP, GET_COMPETENCES, DELETE;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}
