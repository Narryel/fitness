package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class StartTrainingCommandHandler implements CommandHandler {
    @Override
    public SendMessage handleCommand() {
        return null;
    }

    @Override
    public Command commandToHandle() {
        return Command.START_TRAINING_CMD;
    }
}
