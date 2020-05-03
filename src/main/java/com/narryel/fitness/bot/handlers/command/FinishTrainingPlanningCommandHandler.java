package com.narryel.fitness.bot.handlers.command;

import com.narryel.fitness.domain.enums.Command;
import com.narryel.fitness.exceptions.EntityNotFoundException;
import com.narryel.fitness.repository.FitUserRepository;
import com.narryel.fitness.repository.TrainingRepository;
import com.narryel.fitness.util.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.narryel.fitness.domain.enums.Command.FINISH_TRAINING_PLANNING_CMD;

@Service
@RequiredArgsConstructor
public class FinishTrainingPlanningCommandHandler implements CommandHandler {
    private final FitUserRepository fitUserRepository;
    private final TrainingRepository trainingRepository;
    private final MessageGenerator messageGenerator;

    @Override
    @Transactional
    public SendMessage handleCommand(Long chatId) {
        final var user = fitUserRepository.findByChatId(chatId)
                .orElseThrow(EntityNotFoundException::new);
        trainingRepository.finishPlanningUserTraining(user);
        return messageGenerator.getMenu(chatId);
    }

    @Override
    public Command commandToHandle() {
        return FINISH_TRAINING_PLANNING_CMD;
    }
}
