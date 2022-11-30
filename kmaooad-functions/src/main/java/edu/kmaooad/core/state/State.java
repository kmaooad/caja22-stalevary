package edu.kmaooad.core.state;

public interface State {

    default String key() {
        return getClass().getName();
    }

    interface Group {
        default String group() {
            return getClass().getName();
        }
    }

    record Any() implements State {
    }
}
