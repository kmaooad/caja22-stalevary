package edu.kmaooad.app.course.bulk_import_projects;

import edu.kmaooad.core.state.State;

public enum CourseProjectsImport implements State, State.Group {
    GROUP, GET_COURSE_ID, GET_FILE;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}
