package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CommandHandler {

    SendMessage handleCommand();
    Command commandToHandle();
}
