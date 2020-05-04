package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class FinishExerciseCommandHandler implements CommandHandler{

    private final UserStateRepository stateRepository;

    @Override
    public SendMessage handleCommand(Update update) {

        stateRepository.deleteByChatId(getChatId(update));


        return null;
    }

    @Override
    public Command commandToHandle() {
        return Command.FINISH_EXERCISE;
    }
}
