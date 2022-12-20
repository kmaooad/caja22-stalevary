package edu.kmaooad.app.course.update;

import edu.kmaooad.core.state.State;

public enum CourseUpdate  implements State, State.Group {
    GROUP, GET_ID, GET_FIELD, GET_VALUE;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}

