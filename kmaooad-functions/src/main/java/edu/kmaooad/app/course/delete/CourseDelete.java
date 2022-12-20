package edu.kmaooad.app.course.delete;

import edu.kmaooad.core.state.State;

public enum CourseDelete implements State, State.Group {
    GROUP, GET_ID;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}
