package edu.kmaooad.core;

import edu.kmaooad.core.filters.Filter;
import edu.kmaooad.core.state.State;
import edu.kmaooad.core.state.StateMachine;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

public interface Handler {

    default void handle(Message message, StateMachine stateMachine) {
    }

    default void handle(CallbackQuery callbackQuery, StateMachine stateMachine) {
    }


    default List<String> getCommands() {
        return new ArrayList<>();
    }

    default State getState() {
        return new State.Any();
    }

    default Filter getFilter() {
        return null;
    }
}
