package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.util.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class GetMenuCommandHandler extends CommandHandler {

    private final MessageGenerator messageGenerator;

    @NotNull
    @Override
    public Command commandToHandle() {
        return Command.GET_MENU;
    }

    @Override
    public SendMessage handleCommand(Update update) {
        return messageGenerator.getMenu(Long.valueOf(getChatId(update)));
    }
}
