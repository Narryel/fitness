package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class StartTrainingCommandHandler implements CommandHandler {


    @Override
    public SendMessage handleCommand(Long chatId) {
        return null;
    }

    @Override
    public Command commandToHandle() {
        return null;
    }
}
