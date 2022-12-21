package edu.kmaooad.app.course.bulk_import;

import edu.kmaooad.core.state.State;

public enum CoursesImport implements State, State.Group {
    GROUP, GET_FILE;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}
