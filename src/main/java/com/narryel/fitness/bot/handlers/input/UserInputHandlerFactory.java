package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.domain.enums.State;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toUnmodifiableMap;

@Component
public class UserInputHandlerFactory {

    private final Map<State, UserInputHandler> handlerMap;

    public UserInputHandlerFactory(List<UserInputHandler> handlers) {
        handlerMap = handlers
                .stream()
                .collect(toUnmodifiableMap(
                        UserInputHandler::stateToHandle,
                        handler -> handler,
                        (oldHandler, newHandler) -> {
                            throw new IllegalStateException("duplicated state " + newHandler.stateToHandle());
                        }
                ));
    }

    public UserInputHandler getHandlerByState(State state) {
        return Optional.ofNullable(handlerMap.get(state))
                .orElseThrow(() -> new UnsupportedOperationException("no handler found for state " + state));
    }
}
