package edu.kmaooad.core;

import edu.kmaooad.core.state.StateMachine;
import edu.kmaooad.core.session.ISessionService;
import edu.kmaooad.core.session.UserSession;
import edu.kmaooad.exception.IncorrectIdException;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Dispatcher {

    private final List<Handler> handlers = new ArrayList<>();

    private final ISessionService sessionService;
    private final StateMachine stateMachine;

    public Dispatcher(StateMachine stateMachine, ISessionService sessionService) {
        this.stateMachine = stateMachine;
        this.sessionService = sessionService;
    }


    public void registerHandler(Handler handler) {
        handlers.add(handler);
    }

    public boolean dispatch(Message message) {
        Optional<Handler> commandHandler = handlers.stream()
                .filter(h -> h.getCommands() != null
                        && h.getCommands().stream().anyMatch(c -> ("/" + c).equalsIgnoreCase(message.getText())))
                .findFirst();

        if (commandHandler.isPresent()) {
            commandHandler.get().handle(message, stateMachine);
            return true;
        }

        Optional<Handler> stateHandler = getHandlerByState(message.getChatId());
        if (stateHandler.isPresent()) {
            stateHandler.get().handle(message, stateMachine);
            return true;
        }

        Optional<Handler> filterHandler = handlers.stream()
                .filter(h -> h.getFilter() != null
                        && h.getFilter().isApplicable(message))
                .findFirst();

        if (filterHandler.isPresent()) {
            filterHandler.get().handle(message, stateMachine);
            return true;
        } else {
            return false;
        }
    }

    public boolean dispatch(CallbackQuery callbackQuery) {
        Optional<Handler> stateHandler = getHandlerByState(callbackQuery.getMessage().getChatId());
        if (stateHandler.isPresent()) {
            stateHandler.get().handle(callbackQuery, stateMachine);
            return true;
        }

        return false;
    }

    private Optional<Handler> getHandlerByState(Long chatId) {
        UserSession userSession = sessionService
                .getSessionByUserId(chatId)
                .orElseGet(() -> {
                    try {
                        return sessionService.createSessionByUserId(chatId);
                    } catch (IncorrectIdException e) {
                        throw new RuntimeException(e);
                    }
                });

        return handlers.stream()
                .filter(h -> h.getState() != null
                        && h.getState().key().equalsIgnoreCase(userSession.currentState()))
                .findFirst();
    }
}
