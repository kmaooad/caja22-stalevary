package edu.kmaooad.app.course;

import edu.kmaooad.core.state.State;

public enum CourseCreate implements State, State.Group {
    GROUP, GET_TITLE, GET_DESCRIPTION;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}
