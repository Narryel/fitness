package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommandHandlerFactory {

    private final List<CommandHandler> handlers;
    private final Map<Command, CommandHandler> handlerMap = new HashMap<>();

    @PostConstruct
    private void fillHandlerMap() {
        for (CommandHandler handler : handlers) {
            handlerMap.put(handler.commandToHandle(), handler);
        }

    }

    public CommandHandler getHandler(Command cmd) {
        return handlerMap.get(cmd);
    }

}
