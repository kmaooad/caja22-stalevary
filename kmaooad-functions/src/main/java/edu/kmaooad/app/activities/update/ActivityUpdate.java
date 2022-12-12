package edu.kmaooad.app.activities.update;

import edu.kmaooad.core.state.State;

public enum ActivityUpdate  implements State, State.Group {
    GROUP, GET_ID;

    @Override
    public String key() {
        return getClass().getName() + "." + name();
    }
}

