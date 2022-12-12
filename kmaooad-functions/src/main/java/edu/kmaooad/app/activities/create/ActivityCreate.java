package edu.kmaooad.app.activities.create;

import edu.kmaooad.core.state.State;


public enum ActivityCreate implements State, State.Group {
    GROUP, GET_TITLE, GET_DESCRIPTION, GET_DATE, GET_TIME, GET_LOCATION;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}
