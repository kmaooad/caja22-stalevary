package edu.kmaooad.app.activities.delete;

import edu.kmaooad.core.state.State;

public enum ActivityDelete  implements State, State.Group {
    GROUP, GET_ID;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}
