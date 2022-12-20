package edu.kmaooad.app.competence.list;

import edu.kmaooad.core.state.State;

public enum CompetenceList implements State, State.Group{
    GROUP, GET_COMPETENCES;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}
