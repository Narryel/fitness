package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toUnmodifiableMap;

@Component
public class CommandHandlerFactory {

    private final Map<Command, CommandHandler> handlerMap;

    public CommandHandlerFactory(List<CommandHandler> handlers) {
        handlerMap = handlers
                .stream()
                .collect(toUnmodifiableMap(
                        CommandHandler::commandToHandle,
                        handler -> handler,
                        (oldHandler, newHandler) -> {
                            throw new IllegalStateException("duplicated command " + newHandler.commandToHandle());
                        }
                ));
    }

    public CommandHandler getHandlerByCommand(Command cmd) {
        return Optional.ofNullable(handlerMap.get(cmd))
                .orElseThrow(() -> new UnsupportedOperationException("no handler found for command " + cmd));
    }

}
