package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {

    SendMessage handleCommand(Update update);

    Command commandToHandle();

    default Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    default String getData(Update update) {
        return update.getMessage().getText();
    }
}
