package com.narryel.fitness.bot.handlers.input;

import com.narryel.fitness.domain.enums.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserInputHandlerFactory {

    private final List<UserInputHandler> handlers;
    private final Map<State, UserInputHandler> handlerMap = new HashMap<>();

    @PostConstruct
    private void fillHandlerMap() {
        for (UserInputHandler handler : handlers) {
            handlerMap.put(handler.stateToHandle(), handler);
        }

    }

    public UserInputHandler getHandler(State state) {
        return handlerMap.get(state);
    }
}
